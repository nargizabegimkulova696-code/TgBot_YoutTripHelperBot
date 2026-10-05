package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс BotLogicTest тестирует логику бота
 */
class BotTest {
    /**
     * Поле botLogic хранит объект логики бота, который проверяется в тестах
     */
    private final BotLogic botLogic = new BotLogic();

    /**
     * Проверка работы эхо-сообщений
     * @param input текст, который отправил пользователь
     * @param expected ответ, который должен вернуть бот
     */
    @ParameterizedTest
    @CsvSource({
            "привет, 'Вы ввели «привет»'",
            "hello world, 'Вы ввели «hello world»'",
            "start, 'Вы ввели «start»'",
            "/helpme, 'Вы ввели «/helpme»'"
    })
    void echoesOrdinaryText(String input, String expected) {
        assertEquals(expected, botLogic.makeMessage(input));
    }

    /**
     * Проверка работы команды start
     */
    @Test
    void startCommandReturnsGreeting() {
        String expected = "Привет, я Эхо-Бот, отправь мне свое сообщение и я повторю его за тобой!";
        assertEquals(expected, botLogic.makeMessage("/start"));
    }

    /**
     * Проверка работы команды help
     */
    @Test
    void helpCommandListsCommands() {
        String expected = """
                Привет, я Эхо-Бот
                /start - начало работы
                /help - показать все доступные команды""";
        assertEquals(expected, botLogic.makeMessage("/help"));
    }
}