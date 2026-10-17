package org.example;

import org.example.CityResponse.Button;
import org.example.CityResponse.DialogResponse;
import org.example.SearchCityByName.CityCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
    TgBot(String username, String token, CityCatalog catalog) {
        super(token);
        botUsername = username;
        botLogic = new BotLogic(catalog);
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

        if(update.getMessage() == null){
            return;
        }

        Message message = update.getMessage();

        if(!message.hasText()){
            return;
        }

        String text = message.getText();
        long chatId = message.getChatId();

        DialogResponse answer = botLogic.makeMessage(text);

        SendMessage response = toSendMessage(chatId, answer);

        sendMessage(response);

    }

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

    private void handleCallback(CallbackQuery callback) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callback.getId());
        answer.setText("Ответ скоро поступит");

        try {
            execute(answer);
        } catch (TelegramApiException e) {
            log.error("не удалось отправить ответ на Inline кнопку");
        }
    }

    private List<List<InlineKeyboardButton>> createKeyboard(List<List<Button>> fakeKeyboard) {
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

    private SendMessage toSendMessage(long chatId, DialogResponse reply) {
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText(reply.getTextMessage());

        if (reply.hasButtons()) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

            keyboard.setKeyboard(createKeyboard(reply.getListOfCommands()));

            message.setReplyMarkup(keyboard);
        }

        return message;
    }

    /**
     * Метод создан для отправки ответного сообщения
     */
    private void sendMessage(SendMessage message) {
        try{
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Не удалось отправить сообщение", e);
        }
    }
}
