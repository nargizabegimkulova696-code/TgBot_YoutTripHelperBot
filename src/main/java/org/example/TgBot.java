package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * В классе TgBot описана логика работы Эхо-Бота
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
    private final BotLogic botLogic = new BotLogic();

    /**
     * Конструктор TgBot присваивает значению полю botUsername и передает token в конструктор родителя
     */
    TgBot(String username, String token){
        super(token);
        botUsername = username;
    }

    @Override
    public String getBotUsername(){
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update){
        if(update.getMessage() == null){
            return;
        }

        Message message = update.getMessage();

        if(!message.hasText()){
            return;
        }

        String text = message.getText();
        long chatId = message.getChatId();

        String answer = botLogic.makeMessage(text);

        sendMessage(chatId, answer);
    }

    /**
     * Метод создан для отправки ответного сообщения
     */
    private void sendMessage(long chatId, String answer){
        SendMessage reply = new SendMessage();
        reply.setChatId(chatId);
        reply.setText(answer);

        try{
            execute(reply);
        } catch (TelegramApiException e) {
            log.error("Не удалось отправить сообщение", e);
        }
    }
}
