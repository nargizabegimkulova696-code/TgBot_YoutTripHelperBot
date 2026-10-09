package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Тестирует логику бота {@link BotLogic}
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
    void testEchoesOrdinaryText(String input, String expected) {
        Assertions.assertEquals(expected, botLogic.makeMessage(input));
    }
}