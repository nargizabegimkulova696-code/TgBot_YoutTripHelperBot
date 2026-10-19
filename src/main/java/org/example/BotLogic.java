package org.example;

import org.example.Catalog.CityCatalog;
import org.example.DialogService.DialogResponse;
import org.example.SearchCityByName.CityValidator;
import org.example.Session.BotSession;
import org.example.Session.DialogState;
import org.example.Session.SessionStorage;

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
    private static final String START_MESSAGE = "Привет, я бот — помощник в путешествиях. В какой город вы хотели бы отправиться?";

    /**
     * Константные сообщения для команд /help
     */
    private static final String HELP_MESSAGE = """
            Я бот — помощник в путешествиях
            
            /start - начало работы
            /help - показать все доступные команды
            
            Доступные команды после выбора города:
            about_city - краткая сводка о городе
            weather - погода в городе
            back - вернуться к списку команд""";

    private final CityValidator validator;

    /**
     * Создаёт логику бота, работающую с переданным хранилищем сессий
     */
    public BotLogic(SessionStorage sessionStorage, CityCatalog catalog) {
        this.sessionStorage = sessionStorage;
        this.validator = new CityValidator(catalog);
    }

    /**
     * Метод создан для создания сообщения в ответ пользователю
     * @param chatId идентификатор чата пользователя
     * @param text   текст сообщения, которое отправил пользователь
     * @return ответ бота
     */
    public DialogResponse makeMessage(String text, long chatId) {
        BotSession session = sessionStorage.getSession(chatId);
        DialogResponse response = new DialogResponse();

        switch (text) {
            case "/start" -> {
                response.setAnswer(START_MESSAGE);
                session.setState(DialogState.WAITING_FOR_CITY);
                response.changeKeyboardState(session.getState());
            }
            case "/help" -> {
                response.setAnswer(HELP_MESSAGE);
                session.setState(DialogState.WAITING_FOR_CITY);
                response.changeKeyboardState(session.getState());
            }
            default -> {
                if (validator.contain(text)) {
                    response.setAnswer("Выберете категорию для города " + text + ":");
                    session.setCity(text);
                    session.setState(DialogState.CITY_SELECTED);
                    response.changeKeyboardState(session.getState());
                } else {
                    response.setAnswer("Информации о городе не найдено");
                    session.setState(DialogState.WAITING_FOR_CITY);
                    response.changeKeyboardState(session.getState());
                }
            }
        }

        return response;
    }
}