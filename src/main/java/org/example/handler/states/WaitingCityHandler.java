package org.example.handler.states;

import org.example.handler.StateHandler;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.server.CityServer;
import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Состояние WAITING_FOR_CITY: пользователь вводит название города
 */
public class WaitingCityHandler implements StateHandler {
    /**
     * Ответ, если город не найден
     */
    private static final String NOT_FOUND_CITY = "Информации о городе не найдено";

    /**
     * Доступ к каталогу городов
     */
    private final CityServer server;

    /**
     * Кнопки с категориями
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    /**
     * Создание от сервера WaitingCityHandler
     */
    public WaitingCityHandler(CityServer server) {
        this.server = server;
    }

    @Override
    public DialogResponse handle(String text, BotSession session) {
        String city = server.findCityByName(text);
        if (city == null) {
            return new DialogResponse(NOT_FOUND_CITY);
        }
        session.setState(DialogState.CITY_SELECTED);
        session.setCity(city);
        return new DialogResponse("Выберете категорию для города " + city + ":",
                keyboard.build(session.getState()));
    }
}