package org.example.handler;

import org.example.response.DialogResponse;
import org.example.session.BotSession;

/**
 * Обработчик текстового сообщения в конкретном состоянии диалога
 */
public interface StateHandler {
    /**
     * Обрабатывает текст пользователя
     *
     * @param text    сообщение пользователя
     * @param session сессия пользователя
     * @return ответ бота
     */
    DialogResponse handle(String text, BotSession session);
}
