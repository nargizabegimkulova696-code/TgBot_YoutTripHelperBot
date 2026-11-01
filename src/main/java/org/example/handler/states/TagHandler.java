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

    private final static String NOT_FOUND_TAG = "Места для тега не найдены(";

    private static final String FOOD_ANS = "Места, где можно вкусно покушать:";

    private static final String CULTURE_ANS = "Места, которые стоит посетить любителям культуры:";

    private static final String ATTRACTIONS_ANS = "Места для отдыха:";

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
                places = server.getPlaces(session.getCity(), text);
                session.setState(DialogState.IN_TAG_FOOD);

                if (places == null) {
                    return new DialogResponse(NOT_FOUND_TAG, keyboard.build(session.getState()));
                }

                return new DialogResponse(FOOD_ANS + "\n\n" + places, keyboard.build(session.getState()));
            }

            case CAT_CULTURE -> {
                places = server.getPlaces(session.getCity(), text);
                session.setState(DialogState.IN_TAG_CULTURE);

                if (places == null) {
                    return new DialogResponse(NOT_FOUND_TAG, keyboard.build(session.getState()));
                }

                return new DialogResponse(CULTURE_ANS + "\n\n" + places, keyboard.build(session.getState()));
            }

            default -> {
                places = server.getPlaces(session.getCity(), text);
                session.setState(DialogState.IN_TAG_ATTRACTIONS);

                if (places == null) {
                    return new DialogResponse(NOT_FOUND_TAG, keyboard.build(session.getState()));
                }

                return new DialogResponse(ATTRACTIONS_ANS + "\n\n" + places, keyboard.build(session.getState()));
            }
        }
    }
}
