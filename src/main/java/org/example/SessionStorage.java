package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранилище сессий пользователей для каждого чата хранит свою сессию
 */
public class SessionStorage {
    /** Сессии пользователей:
     * 1. ключ это идентификатор чата
     * 2. значение это сессия пользователя.
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
     *
     * @param chatId идентификатор чата
     * @return сессия пользователя
     */
    public BotSession getSession(long chatId) {
        return sessions.computeIfAbsent(chatId, id -> new BotSession());
    }

    /**
     * Удаляет сессию пользователя
     *
     * @param chatId идентификатор чата
     */
    public void removeSession(long chatId) {
        sessions.remove(chatId);
    }

    /**
     * Возвращает количество хранимых сессий.
     *
     * @return число сессий
     */
    public int getSessionCount() {
        return sessions.size();
    }
}