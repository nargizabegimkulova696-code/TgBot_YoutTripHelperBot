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
import org.example.utilities.ChangeState;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Обработчик действий, внутри состояния EDIT_PLACE, CHANGE_NAME, CHANGE_ADDRESS
 */
public class EditPlaceStateHandler implements StateHandler {
    /**
     * Сервер для запросов к бд
     */
    private final TripServer server;

    /**
     * Ответ на некорректное сообщение
     */
    private static final String WRONG_INPUT = "Чтобы изменить место, нужно ввести его номер";

    /**
     * Ошибка при запросе к API мест
     */
    private static final String ERROR = "Ошибка сервера, повторите попытку";

    /**
     * Ошибка сохранения места
     */
    private static final String ERROR_SAVE = "Ошибка сохранения измененного места, повторите попытку позже";

    /**
     * Ошибка при попытке изменить место от API мест
     */
    private static final String WRONG_NUMBER = "Вы не можете редактировать места, которые были в списке изначально. " +
            "Выберете номер места, которое вы добавили в список.";

    /**
     * Вопрос: что будет изменено
     */
    private static final String QUESTION = "\nЧто хотите изменить название или адрес места?";

    /**
     * Меняем имя
     */
    private static final String CHANGE_NAME = "Введите новое название";

    /**
     * Меняем адрес
     */
    private static final String CHANGE_ADDRESS = "Введите новый адрес";

    /**
     * Выбор действия сохранить/отменить
     */
    private static final String CHANGE_APP = "\nВыберете:";

    /**
     * Место успешно изменено
     */
    private static final String SAVE_ANSWER = "Место успешно изменено\n\n";

    /**
     * Сообщение при отмене изменений
     */
    private static final String CANCEL_ANSWER = "Место не изменено\n\n";

    /**
     * Клавиатура кнопок, для ответных сообщений
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    /**
     * Конструктор класса
     *
     * @param server - TripServer, для работы с бд
     */
    public EditPlaceStateHandler(TripServer server) {
        this.server = server;
    }

    @Override
    public DialogResponse handle(String text, BotSession session) {
        switch (text) {
            case "Название", "название" -> {
                session.setState(DialogState.CHANGE_NAME);
                return new DialogResponse(CHANGE_NAME);
            }

            case "Адрес", "адрес" -> {
                session.setState(DialogState.CHANGE_ADDRESS);
                return new DialogResponse(CHANGE_ADDRESS);
            }

            case "com:save" -> {
                UserPlace place = session.getPlace();

                if (!server.editPlace(session.getChatId(), place.getId(),
                        place.getName(), place.getAddress())) {
                    return new DialogResponse(ERROR_SAVE, keyboard.build(session.getState()));
                }

                ChangeState.changeState(session);

                PageCallback callback = new PageCallback(session.getTag());

                Page<Place> page = server.getPage(session.getChatId(), session.getCity(),
                        callback.getTag(), callback.getPage());

                if (page == null || page.isEmpty()) {
                    return new DialogResponse(ERROR, keyboard.build(session.getState()));
                }

                String answer = SAVE_ANSWER + server.formatted(page) +
                        "\n\nСтраница " + (page.getNumber() + 1) + " из " + page.getTotalPages();

                return new DialogResponse(answer, keyboard.buildPage(callback.getTag(), page, true));
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

                switch (session.getState()) {
                    case CHANGE_NAME -> {
                        session.setState(DialogState.EDIT_PLACE);

                        session.getPlace().setName(text);

                        return new DialogResponse(session.getPlace().format() + CHANGE_APP,
                                keyboard.buildChoose());
                    }

                    case CHANGE_ADDRESS -> {
                        session.setState(DialogState.EDIT_PLACE);

                        session.getPlace().setAddress(text);

                        return new DialogResponse(session.getPlace().format() + CHANGE_APP,
                                keyboard.buildChoose());
                    }
                }

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

                        return new DialogResponse(question);

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
