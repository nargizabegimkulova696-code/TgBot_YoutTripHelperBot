package org.example;

import org.example.CityResponse.Button;
import org.example.CityResponse.DialogResponse;
import org.example.SearchCityByName.CityCatalog;
import org.example.SearchCityByName.CityValidator;

import java.util.ArrayList;
import java.util.List;

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
    private static final String START_MESSAGE = "Привет, я бот — помощник в путешествиях. В какой город вы хотели бы отправиться?";

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

    private final CityValidator validator;

    /**
     * Создаёт логику бота, работающую с переданным хранилищем сессий
     */
    public BotLogic(SessionStorage sessionStorage, CityValidator catalog) {
        this.sessionStorage = sessionStorage;
        this.validator = new CityValidator(catalog);
    }

    /**
     * Метод создан для создания сообщения в ответ пользователю
     * @param chatId идентификатор чата пользователя
     * @param text   текст сообщения, которое отправил пользователь
     * @return ответ бота
     */
    public DialogResponse makeMessage(String text) {
        DialogResponse answer;
        //switch case
        if (text.equals("/start")) {
            answer = new DialogResponse(START_MESSAGE);
        } else if (text.equals("/help")) {
            answer = new DialogResponse(HELP_MESSAGE);
        } else if (validator.contain(text)) {
            answer = new DialogResponse("Выберете категорию для города " + text + ":"); //редактировать через formatted string

            List<Button> command1 = new ArrayList<>();
            command1.add(new Button("about city", "cat:about_city"));

            List<Button> command2 = new ArrayList<>();
            command2.add(new Button("weather", "cat:weather"));

            answer.addButton(command1);
            answer.addButton(command2);
        } else {
            answer = new DialogResponse("Не удалось найти информацию о городе(");
        }

        return answer;
    }
}