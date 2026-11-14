package org.example.command;

import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;

import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Класс, метод которого создает ответное сообщение при нажатии кнопки back
 */
public class Back {
    /**
     * Текстовое сообщение, которое получает пользователь, когда возвращается в начало диалога с ботом
     */
    private static final String START_MESSAGE = "Привет, я бот — помощник в путешествиях. В какой город вы хотели бы отправиться?";

    /**
     * Текстовое сообщение, которое получит пользователь при выборе категорий food, culture, attractions
     */
    private static final String CAT_PLACES_MESSAGE = "Что вас интересует?";

    /**
     * Метод создает ответное сообщение пользователю при нажатии кнопки back
     * @param session - по сессии пользователя определяется в какое состояние его нужно вернуть
     * @return DialogResponse ответное сообщение, которое получает пользователь, при возврате на итерацию выше
     */
    public DialogResponse back(BotSession session) {
        DialogState state = session.getState();
        KeyboardFactory keyboard = new KeyboardFactory();

        switch (state) {
            case WAITING_FOR_CITY -> {
                return new DialogResponse(START_MESSAGE);
            }

            case CITY_SELECTED -> {
                session.setState(DialogState.WAITING_FOR_CITY);
                return new DialogResponse(START_MESSAGE, keyboard.build(session.getState()));
            }

            case IN_TAG_FOOD -> {
                session.setState(DialogState.CAT_FOOD);
                return new DialogResponse(CAT_PLACES_MESSAGE, keyboard.build(session.getState()));
            }

            case IN_TAG_CULTURE -> {
                session.setState(DialogState.CAT_CULTURE);
                return new DialogResponse(CAT_PLACES_MESSAGE, keyboard.build(session.getState()));
            }

            case IN_TAG_ATTRACTIONS -> {
                session.setState(DialogState.CAT_ATTRACTIONS);
                return new DialogResponse(CAT_PLACES_MESSAGE, keyboard.build(session.getState()));
            }

            default -> {
                session.setState(DialogState.CITY_SELECTED);
                return new DialogResponse("Выберете категорию для города " + session.getCity() + ":",
                        keyboard.build(session.getState()));
            }

        }
    }
}
