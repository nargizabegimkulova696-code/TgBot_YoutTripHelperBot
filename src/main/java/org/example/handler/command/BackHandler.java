package org.example.handler.command;

import org.example.command.Back;
import org.example.handler.ButtonHandler;
import org.example.response.DialogResponse;
import org.example.session.BotSession;

/**
 * Кнопка back: передаёт нажатие команде {@link Back}
 */
public class BackHandler implements ButtonHandler {
    /**
     * Команда, которая возвращает пользователя на шаг назад
     */
    private final Back back = new Back();

    @Override
    public DialogResponse execute(BotSession session) {
        return back.back(session);
    }
}