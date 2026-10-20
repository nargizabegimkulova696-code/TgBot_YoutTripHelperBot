package org.example.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранилище сессий пользователей, для каждого чата своя сессия
 */
public class SessionStorage {
    /** Сессии пользователей:
     * 1. ключ - идентификатор чата
     * 2. значение - сессия пользователя.
     */
    private final Map<Long, BotSession> sessions;

    /**
     * Конструктор с пустым хранилищем
     */
    public SessionStorage() {
        this.sessions = new ConcurrentHashMap<>();
    }

    /**
     * Возвращает сессию пользователя по идентификатору чата.
     * Если сессии нет, создаёт новую и сохраняет её.
     */
    public BotSession getSession(long chatId) {
        return sessions.computeIfAbsent(chatId, newId -> new BotSession());
    }
}