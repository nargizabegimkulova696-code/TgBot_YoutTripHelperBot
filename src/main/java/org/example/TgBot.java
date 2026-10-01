package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * В классе TgBot описана логика работы Эхо-Бота
 */
public class TgBot extends TelegramLongPollingBot{
    /**
     * Поле botUsername создано для хранения имени бота
     */
    private String botUsername;
    /**
     * Поле log создано для вывода ошибок в консоль при отправке ботом сообщения
     */
    private Logger log = LoggerFactory.getLogger("TgBot");

    /**
     * Конструктор TgBot присваивает значению полю botUsername и передает token в конструктор родителя
     * @param username
     * @param token
     */
    TgBot(String username, String token){
        super(token);
        botUsername = username;
    }

    /**
     * Метод создан для возврата имени бота
     * @return String
     */
    @Override
    public String getBotUsername(){
        return botUsername;
    }

    /**
     * Метод создан для принятия и обработки событий от тг
     * @param update Update received
     */
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

        String answer = messageText(text);

        sendMessage(chatId, answer);
    }

    /**
     * Метод создан для создания сообщения в ответ пользователю
     * @param text
     * @return String
     */
    private String messageText(String text){
        String answer;

        if (text.equals("/start") || text.equals("/start@")){
            answer = "Привет, я Эхо-Бот, отправь мне свое сообщение и я повторю его за тобой!";
        }else{
            answer = "Вы ввели " + "«" + text + "»";
        }

        return answer;
    }

    /**
     * Метод создан для отправки ответного сообщения
     * @param chatId
     * @param answer
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
