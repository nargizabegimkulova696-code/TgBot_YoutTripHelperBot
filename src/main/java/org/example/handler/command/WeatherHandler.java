package org.example.handler.command;

import org.example.handler.ButtonHandler;
import org.example.handler.states.WeatherDatesHandler;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Кнопка weather: проверяет, что город выбран, переводит в состояние CAT_WEATHER и просит ввести период.
 * Сами даты потом разбирает {@link WeatherDatesHandler}
 */
public class WeatherHandler implements ButtonHandler {
    /**
     * Ответ, если кнопку нажали до выбора города
     */
    private static final String NO_CITY = "Сначала выберите город, для этого напишите его название)";

    /**
     * Просьба ввести период дат
     */
    private static final String ASK_DATES = "На какие даты вас интересует погода? " +
            "Напишите период в формате дд.мм - дд.мм. "
            + "Длина периода не больше " + WeatherDatesHandler.MAX_DAYS + " дней.";

    /**
     * Кнопки с категориями
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    @Override
    public DialogResponse execute(BotSession session) {
        if (session.getCity() == null) {
            return new DialogResponse(NO_CITY);
        }

        session.setState(DialogState.CAT_WEATHER);
        return new DialogResponse(ASK_DATES, keyboard.build(session.getState()));
    }
}