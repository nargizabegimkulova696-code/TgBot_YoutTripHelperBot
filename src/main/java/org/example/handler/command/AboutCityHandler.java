package org.example.handler.command;

import org.example.handler.ButtonHandler;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Кнопка about_city
 */
public class AboutCityHandler implements ButtonHandler {
    /**
     * Ответ, если кнопку нажали до выбора города
     */
    private static final String NO_CITY = "Сначала выберите город, для этого напишите его название)";


    private static final String STUB_MESSAGE = "какой-то ответ под about city";

    /**
     * Кнопки с категориями
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    @Override
    public DialogResponse execute(BotSession session) {
        if (session.getCity() == null) {
            return new DialogResponse(NO_CITY);
        }

        session.setState(DialogState.CAT_ABOUT_CITY);
        return new DialogResponse(STUB_MESSAGE, keyboard.build(session.getState()));
    }
}