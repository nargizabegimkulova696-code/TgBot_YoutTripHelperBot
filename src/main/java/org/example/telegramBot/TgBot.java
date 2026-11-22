package org.example.telegramBot;

import org.example.BotLogic;
import org.example.response.Button;
import org.example.response.DialogResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
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
     * Логирование ошибок при отправке сообщений ботом
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
     * Создание ответного сообщения, если от пользователя пришло текстовое сообщение
     */
    private void sendMessage(Message inputMessage) {
        String text = inputMessage.getText();
        Long chatId = inputMessage.getChatId();

        DialogResponse response = botLogic.makeMessage(text, chatId);

        if (response.hasPhotoUri()) {
            sendPhoto(chatId, response.getPhotoUri());
        }

        try {
            execute(createMessage(chatId, response));
        } catch (TelegramApiException e) {
            log.error("Не удалось отправить сообщение", e);
        }
    }

    /**
     * Метод отправляет фото города, если оно есть
     *
     * @param chatId - идентификатор пользователя, которому отправляем фото
     * @param uri    - ссылка на фото
     */
    private void sendPhoto(long chatId, String uri) {
        SendPhoto photo = new SendPhoto();

        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(uri));

        try {
            execute(photo);
        } catch (TelegramApiException e) {
            log.error("Не удалось отправить фото", e);
        }
    }

    /**
     * Создает текстовое сообщение + кнопки, если есть
     *
     * @param chatId   - идентификатор пользователя, которому отправится сообщение
     * @param response - отправляемое сообщение
     */
    private SendMessage createMessage(long chatId, DialogResponse response) {
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText(response.getAnswer());

        if (response.hasButtons()) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            keyboard.setKeyboard(createTGKeyboard(response.getListOfCommands()));
            message.setReplyMarkup(keyboard);
        }

        return message;
    }

    /**
     * Удаление сообщения
     * При отправке фото, так как тг редактирует сообщение, если оно CallbackQuery,
     * сообщение пользователю приходит в виде: текст + фото, а не фото + текст,
     * поэтому отправляется новое сообщение фото + текст, а предыдущее удаляется
     */
    private void delete(long chatId, int messageId) {
        DeleteMessage message = new DeleteMessage();

        message.setChatId(chatId);
        message.setMessageId(messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Не удалось удалить сообщение", e);
        }
    }

    /**
     * Создание ответного сообщения при нажатии на Inline кнопки
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

        if (response.hasPhotoUri()) {
            sendPhoto(chatId, response.getPhotoUri());

            try {
                execute(createMessage(chatId, response));
                delete(chatId, messageId);
            } catch (TelegramApiException e) {
                log.error("Не удалось отправить сообщение с фотографией", e);
            }

            return;
        }

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Не удалось ответить на inline-кнопку ", e);
        }
    }

    /**
     * Создания inline-клавиатуры из списка категорий
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
