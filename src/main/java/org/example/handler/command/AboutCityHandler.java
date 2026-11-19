package org.example.handler.command;

import org.example.handler.ButtonHandler;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.service.CityService;
import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Кнопка about_city
 */
public class AboutCityHandler implements ButtonHandler {
    /**
     * Текстовая команда, которая вызывает этот обработчик
     */
    public static final String COMMAND = "/about_city";

    /**
     * Данные кнопки, которая вызывает этот обработчик
     */
    public static final String CALLBACK = "cat:about_city";
    /**
     * Ответ, если кнопку нажали до выбора города
     */
    private static final String NO_CITY = "Сначала выберите город, для этого напишите его название)";

    /**
     * Кнопки с категориями
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    /**
     * Сервис для получения данных о городе
     */
    private final CityService cityService;

    /**
     * Инициализация обработчика кнопки и сервиса
     */
    public AboutCityHandler(CityService cityService) {
        this.cityService = cityService;
    }

    @Override
    public DialogResponse execute(BotSession session) {
        String city = session.getCity();

        if (city == null) {
            return new DialogResponse(NO_CITY);
        }

        session.setState(DialogState.CAT_ABOUT_CITY);
        return new DialogResponse(cityService.findInfoForCity(city), keyboard.build(session.getState()));
    }
}