package org.example.platform;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.example.BotLogic;
import org.example.response.DialogResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Реализует работу с Discord API
 */
public class DiscordBot extends ListenerAdapter {
    /**
     * Токен для подключения бота к Discord
     */
    private final String token;

    /**
     * Поле логики бота
     */
    private final BotLogic botLogic;

    /**
     * Логирование ошибок при обработке сообщений
     */
    private final Logger log = LoggerFactory.getLogger("DiscordBot");

    /**
     * Очередь для обработки сообщений в порядке их поступления
     */
    private final ExecutorService worker = Executors.newSingleThreadExecutor();

    /**
     * Конструктор DiscordBot сохраняет токен и логику бота
     */
    public DiscordBot(String token, BotLogic botLogic) {
        this.token = token;
        this.botLogic = botLogic;
    }

    /**
     * Подключение бота к Discord
     */
    public void start() {
        try {
            JDABuilder.createLight(token, GatewayIntent.DIRECT_MESSAGES)
                    .addEventListeners(this)
                    .build();
        } catch (InvalidTokenException e) {
            log.error("Не удалось подключить бота Discord: неверный токен");
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        log.info("Бот Discord подключён");
        menuRegistration(event.getJDA());
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        worker.shutdownNow();
    }

    /**
     * Создание меню команд в личных сообщениях Discord
     */
    private void menuRegistration(JDA jda) {
        jda.updateCommands().addCommands(
                Commands.slash("start", "Выбрать город").setContexts(InteractionContextType.BOT_DM),
                Commands.slash("help", "Показать помощь").setContexts(InteractionContextType.BOT_DM)
        ).queue(
                commands -> log.info("Меню Discord зарегистрировано"),
                error -> log.error("Не удалось зарегистрировать меню Discord", error)
        );
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.PRIVATE) || event.getAuthor().isBot()) {
            return;
        }

        String text = event.getMessage().getContentRaw();
        if (text.isBlank()) {
            return;
        }

        enqueue(() -> {
            long userId = event.getAuthor().getIdLong();
            DialogResponse response = botLogic.makeMessage(text, userId);

            event.getChannel().sendMessage(createDiscordMessage(response)).complete();
        });
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getChannelType() == ChannelType.PRIVATE) {
            String command = "/" + event.getName();
            long userId = event.getUser().getIdLong();

            respond(event.deferReply().submit(), command, userId);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getChannelType() == ChannelType.PRIVATE) {
            String data = event.getComponentId();
            long userId = event.getUser().getIdLong();

            respond(event.deferEdit().submit(), data, userId);
        }
    }

    /**
     * Создание ответа на команду меню или нажатие кнопки
     */
    private void respond(CompletableFuture<InteractionHook> confirmation,
                         String command, long userId) {
        enqueue(() -> {
            InteractionHook hook = confirmation.join();
            DialogResponse response = botLogic.makeMessage(command, userId);

            MessageEditData message = MessageEditData.fromCreateData(
                    createDiscordMessage(response));
            hook.editOriginal(message).complete();
        });
    }

    /**
     * Выполнение обработки диалога вне потока событий Discord
     */
    private void enqueue(Runnable task) {
        worker.execute(() -> {
            try {
                task.run();
            } catch (RuntimeException e) {
                log.error("Не удалось обработать сообщение Discord", e);
            }
        });
    }

    /**
     * Создание ответного сообщения из текста и кнопок бота
     */
    private MessageCreateData createDiscordMessage(DialogResponse response) {
        MessageCreateBuilder message = new MessageCreateBuilder();
        String text = response.getAnswer();

        if (text.length() <= Message.MAX_CONTENT_LENGTH) {
            message.setContent(text);
        } else {
            message.setContent("Полный ответ - в приложенном файле.");
            message.addFiles(FileUpload.fromData(
                    text.getBytes(StandardCharsets.UTF_8), "answer.txt"));
        }

        message.setAllowedMentions(List.of());
        return message.setComponents(createDiscordKeyboard(response.getListOfCommands())).build();
    }

    /**
     * Создание клавиатуры Discord из списка кнопок ответа
     */
    private List<ActionRow> createDiscordKeyboard(List<List<org.example.response.Button>> keyboard) {
        List<Button> buttons = new ArrayList<>();
        List<ActionRow> rows = new ArrayList<>();

        for (List<org.example.response.Button> buttonRow : keyboard) {
            for (org.example.response.Button button : buttonRow) {
                Button discordButton = Button.secondary(
                        button.getCallBackData(), button.getCommandText());
                buttons.add(discordButton);
            }
        }

        for (int i = 0; i < buttons.size(); i += 3) {
            int end = Math.min(i + 3, buttons.size());
            rows.add(ActionRow.of(buttons.subList(i, end)));
        }

        return rows;
    }
}