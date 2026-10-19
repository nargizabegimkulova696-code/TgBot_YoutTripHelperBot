package org.example;

import org.example.Session.BotSession;
import org.example.Session.DialogState;
import org.example.Session.SessionStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

/**
 * Тестирует логику бота {@link BotLogic}
 */
class BotTest {
    /**
     * Хранилище сессий, общее для логики и тестов: через него тест задаёт состояние пользователя
     */
    private final SessionStorage storage = new SessionStorage();

    /**
     * Поле botLogic хранит объект логики бота, который проверяется в тестах
     */
    private final BotLogic botLogic = new BotLogic(storage);

    /**
     * Первое сообщение нового пользователя создаёт для него сессию в хранилище
     */
    @Test
    void testMessageCreatesSession() {
        Assertions.assertEquals(0, storage.getSessionCount());

        botLogic.makeMessage(42L, "/start");

        Assertions.assertEquals(1, storage.getSessionCount());
    }

    /**
     * Одновременные сообщения от многих пользователей создают по одной сессии на каждого
     */
    @Test
    void testConcurrentUsers() {
        IntStream.range(0, 500).parallel().forEach(chatId -> botLogic.makeMessage(chatId, "/start"));

        Assertions.assertEquals(500, storage.getSessionCount());
    }

    /**
     * Новый пользователь получает сессию в начальном состоянии
     */
    @Test
    void testNewUserGetsInitialSession() {
        BotSession session = storage.getSession(1L);

        Assertions.assertEquals(DialogState.WAITING_FOR_CITY, session.getState());
        Assertions.assertNull(session.getCity());
    }

    /**
     * Один и тот же пользователь получает один и тот же объект сессии, а разные пользователи разные
     */
    @Test
    void testSessionIdentity() {
        Assertions.assertEquals(storage.getSession(1L), storage.getSession(1L));
        Assertions.assertNotEquals(storage.getSession(1L), storage.getSession(2L));
    }

}