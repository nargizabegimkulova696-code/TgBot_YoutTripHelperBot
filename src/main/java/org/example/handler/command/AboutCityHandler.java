package org.example.handler.command;

import org.apache.commons.lang3.StringUtils;
import org.example.handler.ButtonHandler;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.server.CityServer;
import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Кнопка about_city
 */
public class AboutCityHandler implements ButtonHandler {
    /**
     * Ответ, если кнопку нажали до выбора города
     */
    private static final String NO_CITY = "Сначала выберите город, для этого напишите его название)";

    private static final String NO_DATA = "Информации о городе не найдено";

    /**
     * Кнопки с категориями
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    /**
     * Сервер для получения данных о городе
     */
    private final CityServer server;

    /**
     * Инициализация обработчика кнопки и сервера
     */
    public AboutCityHandler(CityServer server) {
        this.server = server;
    }

    @Override
    public DialogResponse execute(BotSession session) {
        String city = session.getCity();

        if (city == null) {
            return new DialogResponse(NO_CITY);
        }

        session.setState(DialogState.CAT_ABOUT_CITY);

        String cityInfo = server.findInfoForCity(city);

        if (StringUtils.isBlank(cityInfo)) {
            return new DialogResponse(NO_DATA, keyboard.build(session.getState()));
        }

        return new DialogResponse(cityInfo, keyboard.build(session.getState()));
    }
}