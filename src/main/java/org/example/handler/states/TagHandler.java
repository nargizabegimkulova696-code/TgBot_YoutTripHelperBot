package org.example.handler.states;

import org.example.command.places.PlacesProvider;
import org.example.handler.StateHandler;
import org.example.paging.Page;
import org.example.paging.PageCallback;
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
     * Ответ, если вместо кнопки прислали текст
     */
    private static final String CHOOSE_TAG = "Выберите, что вас интересует, с помощью кнопок.";

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
        PageCallback callback;
        try {
            callback = new PageCallback(text);
        } catch (IllegalArgumentException e) {
            return new DialogResponse(CHOOSE_TAG, keyboard.build(session.getState()));
        }

        String title;

        switch (session.getState()) {
            case CAT_FOOD, IN_TAG_FOOD -> {
                session.setState(DialogState.IN_TAG_FOOD);
                title = FOOD_ANS;
            }
            case CAT_CULTURE, IN_TAG_CULTURE -> {
                session.setState(DialogState.IN_TAG_CULTURE);
                title = CULTURE_ANS;
            }
            default -> {
                session.setState(DialogState.IN_TAG_ATTRACTIONS);
                title = ATTRACTIONS_ANS;
            }
        }

        Page<PlacesProvider.Place> page = server.getPage(session.getCity(), callback.getTag(), callback.getPage());
        if (page == null || page.isEmpty()) {
            return new DialogResponse(NOT_FOUND_TAG, keyboard.build(session.getState()));
        }

        String answer = title + "\n\n" + server.formatted(page)
                + "\n\nСтраница " + (page.getNumber() + 1) + " из " + page.getTotalPages();

        return new DialogResponse(answer, keyboard.buildPage(callback.getTag(), page));
    }
}
