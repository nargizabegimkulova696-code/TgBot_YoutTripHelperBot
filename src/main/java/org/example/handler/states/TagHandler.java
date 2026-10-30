package org.example.handler.states;

import org.example.handler.StateHandler;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.server.TripServer;
import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Обработчик состояния, в котором находится пользователь
 * после выбора одной из категорий: food, culture, attractions
 */
public class TagHandler implements StateHandler {
    /**
     * Сервер для получения данных о теге по городу
     */
    private final TripServer server;

    /**
     * Клавиатура кнопок
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    /**
     * Инициализация обработчика состояний и сервера
     */
    public TagHandler(TripServer server) {
        this.server = server;
    }

    @Override
    public DialogResponse handle(String text, BotSession session) {
        String places;

        switch (session.getState()) {
            case CAT_FOOD -> {
                places = server.getPlaces(DialogState.CAT_FOOD, session.getCity(), text);
                session.setState(DialogState.IN_TAG_FOOD);

                return new DialogResponse(places, keyboard.build(session.getState()));
            }

            case CAT_CULTURE -> {
                places = server.getPlaces(DialogState.CAT_CULTURE, session.getCity(), text);
                session.setState(DialogState.IN_TAG_CULTURE);

                return new DialogResponse(places, keyboard.build(session.getState()));
            }

            default -> {
                places = server.getPlaces(DialogState.CAT_ATTRACTIONS, session.getCity(), text);
                session.setState(DialogState.IN_TAG_ATTRACTIONS);

                return new DialogResponse(places, keyboard.build(session.getState()));
            }
        }
    }
}
