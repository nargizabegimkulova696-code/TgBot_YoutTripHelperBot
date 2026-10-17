package org.example;

import org.example.session.BotSession;
import org.example.session.DialogState;
import org.example.session.SessionStorage;

/**
 * Логика работы бота, которая не зависит от API
 */
public class BotLogic {
    /**
     * Хранилище сессий пользователей
     */
    private final SessionStorage sessionStorage;

    /**
     * Константные сообщения для команд /start
     */
    private static final String START_MESSAGE = "Привет, я Эхо-Бот, " + "отправь мне свое сообщение и я повторю его за тобой!";

    /**
     * Константные сообщения для команд /help
     */
    private static final String HELP_MESSAGE = """
            Я бот — помощник в путешествиях
            /start - начало работы
            /help - показать все доступные команды
            Доступные команды после выбора города:
            /about_city - краткая сводка о городе
            /weather - погода в городе
            /back - вернуться к списку команд""";


    /**
     * Создаёт логику бота, работающую с переданным хранилищем сессий
     */
    public BotLogic(SessionStorage sessionStorage) {
        this.sessionStorage = sessionStorage;
    }

    /**
     * Метод создан для создания сообщения в ответ пользователю
     * @param chatId идентификатор чата пользователя
     * @param text   текст сообщения, которое отправил пользователь
     * @return ответ бота
     */
    public String makeMessage(long chatId, String text) {

        String answer;

        switch (text) {
            case "/start" -> answer = START_MESSAGE;
            case "/help" -> answer = HELP_MESSAGE;
            default -> answer = "Команда «%s» не найдена. Напишите /help, чтобы увидеть список команд".formatted(text);
        }

        return answer;
    }


}