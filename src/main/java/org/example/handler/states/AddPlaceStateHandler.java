package org.example.handler.states;

import org.example.command.places.Place;
import org.example.handler.StateHandler;
import org.example.paging.Page;
import org.example.paging.PageCallback;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.server.TripServer;
import org.example.session.BotSession;
import org.example.session.DialogState;
import org.example.storage.UserPlace;

/**
 * Обработчик действий, которые происходят в состоянии ADD_PLACE
 */
public class AddPlaceStateHandler implements StateHandler {
    /**
     * Сервер, для запросов к бд
     */
    private final TripServer server;

    /**
     * Сообщение, если название и адрес места введены не по формату или на прочие действия,
     * которые не обрабатываются в этом состоянии
     */
    private static final String WRONG_INPUT = "Некорректный формат ввода";

    /**
     * Вопрос перед добавлением
     */
    private static final String ADD_PLACE = "Добавляю в список место: ";

    /**
     * Исключение от бд
     */
    private static final String SQL_ERROR = "Не удалось добавить место, повторите попытку позже)";

    /**
     * Ошибка при запросе к API мест
     */
    private static final String ERROR = "Не удалось вывести список мест, повторите попытку позже)";

    /**
     * Сообщение при успешном добавлении места
     */
    private static final String SAVE_ANSWER = "Место успешно добавлено в список";

    /**
     * Сообщение, если пользователь решил не добавлять место в список
     */
    private static final String CANCEL_ANSWER = "Место не добавлено в список";

    /**
     * Формат ввода содержит 2 значения: название и адрес
     */
    private static final int DATA = 2;

    /**
     * Заглушка для id в бд
     */
    private static final long ID = 0L;

    /**
     * Клавиатура кнопок, для ответных сообщений
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    /**
     * Конструктор класса
     *
     * @param server - TripServer, для работы с бд
     */
    public AddPlaceStateHandler(TripServer server) {
        this.server = server;
    }

    @Override
    public DialogResponse handle(String text, BotSession session) {
        switch (text) {
            case "com:save" -> {
                UserPlace place = session.getPlace();
                String category = foundCategory(session.getCategory());

                if (!server.addPlace(session.getChatId(), session.getCity(), category,
                        session.getTag(), place.getName(), place.getAddress())) {
                    return new DialogResponse(SQL_ERROR, keyboard.build(session.getState()));
                }

                changeState(session);

                PageCallback callback = new PageCallback(session.getTag());

                Page<Place> page = server.getPage(session.getChatId(), session.getCity(),
                        callback.getTag(), callback.getPage());

                if (page == null || page.isEmpty()) {
                    return new DialogResponse(ERROR, keyboard.build(DialogState.ADD_PLACE));
                }

                String answer = SAVE_ANSWER + "\n\n" + server.formatted(page)
                        + "\n\nСтраница " + (page.getNumber() + 1) + " из " + page.getTotalPages();

                return new DialogResponse(answer, keyboard.buildPage(callback.getTag(), page, true));
            }

            case "com:cancel" -> {
                changeState(session);

                PageCallback callback = new PageCallback(session.getTag());

                Page<Place> page = server.getPage(session.getChatId(), session.getCity(),
                        callback.getTag(), callback.getPage());

                if (page == null || page.isEmpty()) {
                    return new DialogResponse(ERROR, keyboard.build(DialogState.ADD_PLACE));
                }

                String answer = CANCEL_ANSWER + "\n\n" + server.formatted(page)
                        + "\n\nСтраница " + (page.getNumber() + 1) + " из " + page.getTotalPages();

                boolean flag = server.hasUserPlaces(session.getChatId(), session.getCity(), session.getTag());

                return new DialogResponse(answer, keyboard.buildPage(callback.getTag(), page, flag));
            }

            default -> {
                String[] newPlace = text.split(",");

                if (newPlace.length != DATA) {
                    return new DialogResponse(WRONG_INPUT, keyboard.build(session.getState()));
                }

                String name = newPlace[0].trim();
                String address = newPlace[1].trim();
                String category = foundCategory(session.getCategory());

                UserPlace place = new UserPlace(ID, category,
                        session.getTag(), name, address);

                session.setPlace(place);

                return new DialogResponse(ADD_PLACE + place.format(), keyboard.buildChoose());
            }
        }
    }

    private String foundCategory(DialogState category) {
        switch (category) {
            case CAT_FOOD -> {
                return "food";
            }

            case CAT_ATTRACTIONS -> {
                return "attractions";
            }

            default -> {
                return "culture";
            }
        }
    }

    private void changeState(BotSession session) {
        switch (session.getCategory()) {
            case CAT_FOOD -> {
                session.setState(DialogState.IN_TAG_FOOD);
            }

            case CAT_CULTURE -> {
                session.setState(DialogState.IN_TAG_CULTURE);
            }

            default -> {
                session.setState(DialogState.IN_TAG_ATTRACTIONS);
            }
        }
    }
}
