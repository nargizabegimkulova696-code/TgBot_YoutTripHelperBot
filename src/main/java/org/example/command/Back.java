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
     * Метод создает ответное сообщение пользователю при нажатии кнопки back
     *
     * @param session - по сессии пользователя определяется в какое состояние его нужно вернуть
     * @return DialogResponse ответное сообщение, которое получает пользователь, при возврате на итерацию выше
     */
    public DialogResponse back(BotSession session) {
        DialogState state = session.getState();
        DialogResponse newResponse;
        KeyboardFactory keyboard = new KeyboardFactory();

        switch (state) {
            case WAITING_FOR_CITY -> {
                newResponse = new DialogResponse(START_MESSAGE);
            }

            case CITY_SELECTED -> {
                session.setState(DialogState.WAITING_FOR_CITY);
                newResponse = new DialogResponse(START_MESSAGE, keyboard.build(session.getState()));
            }

            default -> {
                session.setState(DialogState.CITY_SELECTED);
                newResponse = new DialogResponse("Выберете категорию для города " + session.getCity() + ":",
                        keyboard.build(session.getState()));
            }

        }

        return newResponse;
    }
}
