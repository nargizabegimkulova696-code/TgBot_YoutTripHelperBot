package org.example;

import org.example.CityResponse.Button;
import org.example.CityResponse.DialogResponse;
import org.example.SearchCityByName.CityCatalog;
import org.example.SearchCityByName.CityValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Логика работы бота, которая не зависит от API
 */
public class BotLogic {
    /**
     * Константные сообщения для команд /start
     */
    private static final String START_MESSAGE = "Привет, я бот — помощник в путешествиях. В какой город вы хотели бы отправиться?";

    /**
     * Константные сообщения для команд /help
     */
    private static final String HELP_MESSAGE = """
            Привет, я Эхо-Бот
            /start - начало работы
            /help - показать все доступные команды""";

    private final CityValidator validator;

    public BotLogic(CityCatalog catalog) {
        validator = new CityValidator(catalog);
    }


    /**
     * Метод создан для создания сообщения в ответ пользователю
     * @param text текст сообщения, которое отправил пользователь
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