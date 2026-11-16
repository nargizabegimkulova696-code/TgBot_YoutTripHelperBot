package org.example.handler.states;

import org.example.command.places.Place;
import org.example.handler.StateHandler;
import org.example.paging.Page;
import org.example.paging.PageCallback;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.server.TripServer;
import org.example.session.BotSession;
import org.example.storage.UserPlace;
import org.example.utilities.ChangeState;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Обработчик действий, внутри состояния DELETE_PLACE
 */
public class DeletePlaceStateHandler implements StateHandler {
    /**
     * Сообщение, если пользователь ввел некорректное сообщение
     */
    private static final String WRONG_INPUT = "Чтобы удалить место, нужно ввести его номер";

    /**
     * Сообщение, если пользователь хочет удалить место из API
     */
    private static final String WRONG_NUMBER = "Вы не можете удалять места, которые были в списке изначально. " +
            "Выберете номер места, которое вы добавили в список.";

    /**
     * Вопрос перед удалением
     */
    private static final String QUESTION = "\nТочно ли вы хотите удалить это место?";

    /**
     * Ошибка при запросе к API мест
     */
    private static final String ERROR = "Ошибка сервера, повторите попытку";

    /**
     * Ошибка при удалении
     */
    private static final String ERROR_DELETE = "Ошибка при удалении места, повторите попытку позже";

    /**
     * Сообщение при успешном удалении места
     */
    private static final String DELETE_ANSWER = "Место успешно удалено\n\n";

    /**
     * Сообщение, если пользователь не стал удалять место
     */
    private static final String CANCEL_ANSWER = "Место не удалено\n\n";

    /**
     * Клавиатура кнопок, для ответных сообщений
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    /**
     * Сервер для запросов к бд
     */
    private final TripServer server;

    /**
     * Конструктор класса
     *
     * @param server - TripServer, для работы с бд
     */
    public DeletePlaceStateHandler(TripServer server) {
        this.server = server;
    }

    @Override
    public DialogResponse handle(String text, BotSession session) {
        switch (text) {
            case "com:save" -> {
                UserPlace place = session.getPlace();

                if (!server.deletePlace(session.getChatId(), place.getId())) {
                    return new DialogResponse(ERROR_DELETE, keyboard.build(session.getState()));
                }

                ChangeState.changeState(session);

                PageCallback callback = new PageCallback(session.getTag());

                Page<Place> page = server.getPage(session.getChatId(), session.getCity(),
                        callback.getTag(), callback.getPage());

                if (page == null || page.isEmpty()) {
                    return new DialogResponse(ERROR, keyboard.build(session.getState()));
                }

                String answer = DELETE_ANSWER + server.formatted(page) +
                        "\n\nСтраница " + (page.getNumber() + 1) + " из " + page.getTotalPages();

                boolean flag = server.hasUserPlaces(session.getChatId(), session.getCity(), session.getTag());

                return new DialogResponse(answer, keyboard.buildPage(callback.getTag(), page, flag));
            }

            case "com:cancel" -> {
                ChangeState.changeState(session);

                PageCallback callback = new PageCallback(session.getTag());

                Page<Place> page = server.getPage(session.getChatId(), session.getCity(),
                        callback.getTag(), callback.getPage());

                if (page == null || page.isEmpty()) {
                    return new DialogResponse(ERROR, keyboard.build(session.getState()));
                }

                String answer = CANCEL_ANSWER + server.formatted(page) +
                        "\n\nСтраница " + (page.getNumber() + 1) + " из " + page.getTotalPages();

                return new DialogResponse(answer, keyboard.buildPage(callback.getTag(), page, true));
            }

            default -> {
                try {

                    int number = Integer.parseInt(text);

                    try {
                        if (server.findUserPlace(session.getChatId(), session.getCity(),
                                session.getTag(), number) == null) {
                            return new DialogResponse(WRONG_NUMBER, keyboard.build(session.getState()));
                        }

                        UserPlace place = server.findUserPlace(session.getChatId(), session.getCity(),
                                session.getTag(), number);

                        session.setPlace(place);

                        String question = number + ") " + place.format() + QUESTION;

                        return new DialogResponse(question, keyboard.buildChoose());

                    } catch (SQLException | IOException e) {
                        return new DialogResponse(ERROR, keyboard.build(session.getState()));
                    }

                } catch (NumberFormatException e) {
                    return new DialogResponse(WRONG_INPUT, keyboard.build(session.getState()));
                }
            }
        }
    }
}
