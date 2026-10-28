package org.example.handler;

import org.example.response.DialogResponse;
import org.example.session.BotSession;


/**
 * Обработчик команды или нажатия кнопки
 */
public interface ButtonHandler {
    /**
     * Выполняет команду для пользователя
     *
     * @param session сессия пользователя
     * @return ответ бота
     */
    DialogResponse execute(BotSession session);
}
