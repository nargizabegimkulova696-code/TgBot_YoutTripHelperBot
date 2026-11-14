package org.example.handler.command;

import org.example.handler.ButtonHandler;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Обработчик категории food
 */
public class FoodHandler implements ButtonHandler {
    /**
     * Ответное сообщение при выборе категории
     */
    private static final String ANSWER = "Что вас интересует?";

    /**
     * Ответное сообщение, если город не выбран
     */
    private static final String NO_CITY = "Сначала выберите город, для этого напишите его название)";

    /**
     * Клавиатура кнопок
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    @Override
    public DialogResponse execute(BotSession session) {
        if (session.getCity() == null) {
            return new DialogResponse(NO_CITY);
        }

        session.setState(DialogState.CAT_FOOD);
        return new DialogResponse(ANSWER, keyboard.build(session.getState()));
    }
}
