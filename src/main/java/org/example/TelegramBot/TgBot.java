package org.example.TelegramBot;

import org.example.BotLogic;
import org.example.DialogService.Button;
import org.example.DialogService.DialogResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


/**
 * Реализует работу с telegram API
 */
public class TgBot extends TelegramLongPollingBot{
    /**
     * Поле botUsername создано для хранения имени бота
     */
    private final String botUsername;
    /**
     * Поле log создано для вывода ошибок в консоль при отправке ботом сообщения
     */
    private final Logger log = LoggerFactory.getLogger("TgBot");

    /**
     * Поле логики бота
     */
    private final BotLogic botLogic;

    /**
     * Конструктор TgBot присваивает значению полю botUsername и передает token в конструктор родителя
     */
    public TgBot(String username, String token, BotLogic botLogic) {
        super(token);

        botUsername = username;
        this.botLogic = botLogic;

        menuRegistration();
    }

    @Override
    public String getBotUsername(){
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update){
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
            return;
        }

        if (update.getMessage() != null && update.getMessage().hasText()) {
            sendMessage(update.getMessage());
        }

    }

    /**
     * Создание меню в тг-боте
     */
    private void menuRegistration() {
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("start", "начать"));
        commands.add(new BotCommand("help", "помощь"));

        try {
            SetMyCommands setCommands = new SetMyCommands(commands, new BotCommandScopeDefault(), null);
            execute(setCommands);

            log.info("связывание меню с телеграмом выполнено успешно");
        } catch (TelegramApiException e) {
            log.error("Не удалось связать меню с телеграмом", e);
        }

    }

    /**
     * Создание ответного сообщение для телеграма
     */
    private void sendMessage(Message inputMessage) {
        String text = inputMessage.getText();
        Long chatId = inputMessage.getChatId();

        DialogResponse response = botLogic.makeMessage(text, chatId);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(response.getAnswer());

        if (response.hasButtons()) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

            keyboard.setKeyboard(createTGKeyboard(response.getListOfCommands()));

            message.setReplyMarkup(keyboard);
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Не удалось отправить сообщение", e);
        }
    }

    /**
     * Ответ на нажатие Inline кнопки
     */
    private void handleCallback(CallbackQuery callback) {
        long chatId = callback.getMessage().getChatId();
        int messageId = callback.getMessage().getMessageId();
        String data = callback.getData();

        DialogResponse response = botLogic.makeMessage(data, chatId);

        EditMessageText editMessage = new EditMessageText();
        editMessage.setText(response.getAnswer());
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);

        if (response.hasButtons()) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            keyboard.setKeyboard(createTGKeyboard(response.getListOfCommands()));

            editMessage.setReplyMarkup(keyboard);
        }

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Не удалось ответить на inline-кнопку ", e);
        }
    }

    /**
     * Создание Inline клавиатуры
     */
    private List<List<InlineKeyboardButton>> createTGKeyboard(List<List<Button>> fakeKeyboard) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (List<Button> buttonRows : fakeKeyboard) {
            List<InlineKeyboardButton> tgRow = new ArrayList<>();

            for (Button button : buttonRows) {
                InlineKeyboardButton tgButton = new InlineKeyboardButton();

                tgButton.setText(button.getCommandText());
                tgButton.setCallbackData(button.getCallBackData());

                tgRow.add(tgButton);
            }

            keyboard.add(tgRow);
        }

        return keyboard;
    }
}
