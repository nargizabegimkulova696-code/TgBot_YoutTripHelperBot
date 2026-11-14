package org.example.handler.states;

import org.example.command.places.PlacesProvider.Place;
import org.example.handler.StateHandler;
import org.example.paging.Page;
import org.example.paging.PageCallback;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.service.TripService;
import org.example.session.BotSession;
import org.example.session.DialogState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Обработчик выбора тега и листания мест.
 * Работает в состояниях CAT_* (ждём тег) и IN_TAG_* (смотрим места);
 * При нажатии кнопки получаем строку callback_data,
 * где тег и номер страницы закодированы в ней (разборка с помощью {@link PageCallback});
 *
 * Пример: "tag:cafe" - первая страница тега, "page:tag:cafe:2" - страница 2 того же тега.
 */
public class TagHandler implements StateHandler {

    /**
     * Ответное сообщение, если мест для города не найдено
     */
    private final static String NOT_FOUND_TAG = "Места для тега не найдены(";

    /**
     * Ответное сообщение пользователю из состояний CAT_FOOD и IN_TAG_FOOD
     */
    private static final String FOOD_ANS = "Места, где можно вкусно покушать:";

    /**
     * Ответное сообщение пользователю из состояний CAT_CULTURE и IN_TAG_CULTURE
     */
    private static final String CULTURE_ANS = "Места, которые стоит посетить любителям культуры:";

    /**
     * Ответное сообщение пользователю из состояний CAT_ATTRACTIONS и IN_TAG_FOOD
     */
    private static final String ATTRACTIONS_ANS = "Места для отдыха:";

    /**
     * Ответ, если вместо кнопки прислали текст
     */
    private static final String CHOOSE_TAG = "Выберите, что вас интересует, с помощью кнопок.";

    /**
     * Ответное сообщение, если источник мест не ответил из-за ошибки
     */
    private static final String PLACES_UNAVAILABLE = "Не удалось загрузить места. Попробуйте позже.";

    /**
     * Сервис для получения данных о теге по городу
     */
    private final TripService tripService;

    /**
     * Логирование ошибок получения мест
     */
    private final Logger log = LoggerFactory.getLogger(TagHandler.class);

    /**
     * Клавиатура кнопок
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    /**
     * Инициализация обработчика состояний и сервиса
     */
    public TagHandler(TripService tripService) {
        this.tripService = tripService;
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

        Page<Place> page;
        try {
            page = tripService.getPage(session.getCity(), callback.getTag(), callback.getPage());
        } catch (IOException e) {
            log.error("Не удалось получить места: город={}, тег={}", session.getCity(), callback.getTag(), e);
            return new DialogResponse(PLACES_UNAVAILABLE, keyboard.build(session.getState()));
        }

        if (page.isEmpty()) {
            return new DialogResponse(NOT_FOUND_TAG, keyboard.build(session.getState()));
        }

        return new DialogResponse(formatPage(title, page), keyboard.buildPage(callback.getTag(), page));
    }

    /**
     * Собирает текст ответа со страницей мест: заголовок, места через пустую строку и номер страницы
     *
     * @param title заголовок категории
     * @param page  страница мест
     * @return текст сообщения пользователю
     */
    private String formatPage(String title, Page<Place> page) {
        List<String> blocks = new ArrayList<>();

        for (Place place : page.getItems()) {
            blocks.add("- " + place.format());
        }

        return title + "\n\n" + String.join("\n\n", blocks)
                + "\n\nСтраница " + (page.getNumber() + 1) + " из " + page.getTotalPages();
    }
}
