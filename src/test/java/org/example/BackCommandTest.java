package org.example;

import org.example.command.Back;

import org.example.session.BotSession;
import org.example.session.DialogState;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Тестирование команды и кнопки back
 */
public class BackCommandTest {
    /**
     * Сессия пользователя
     */
    BotSession session = new BotSession();

    /**
     * Инициализация back
     */
    Back back = new Back();

    /**
     * Тестирование команды на корректное возвращение из начало диалога в начало диалога
     */
    @Test
    void BackFromStart() {
        back.back(session);

        Assertions.assertEquals(DialogState.WAITING_FOR_CITY, session.getState());
    }

    /**
     * Тестирование команды на возврат из списка категорий в начало диалога
     */
    @Test
    void BackFromCatForCity() {
        session.setState(DialogState.CITY_SELECTED);
        back.back(session);

        Assertions.assertEquals(DialogState.WAITING_FOR_CITY, session.getState());
    }

    /**
     * Тестирование команды на возврат из команд в список категорий
     */
    @Test
    void BackFromAboutCityAndCatWeather() {
        session.setState(DialogState.CAT_ABOUT_CITY);
        back.back(session);

        Assertions.assertEquals(DialogState.CITY_SELECTED, session.getState());

        session.setState(DialogState.CAT_WEATHER);
        back.back(session);

        Assertions.assertEquals(DialogState.CITY_SELECTED, session.getState());
    }

    /**
     * Тестирование последовательного возвращения из команд к началу диалога
     */
    @Test
    void BackFromCatAboutCityAndCatWeatherToStart() {
        session.setState(DialogState.CAT_ABOUT_CITY);
        back.back(session);
        back.back(session);

        Assertions.assertEquals(DialogState.WAITING_FOR_CITY, session.getState());

        session.setState(DialogState.CAT_WEATHER);
        back.back(session);
        back.back(session);

        Assertions.assertEquals(DialogState.WAITING_FOR_CITY, session.getState());
    }
}
