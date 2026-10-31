package org.example.handler.command;

import org.example.handler.ButtonHandler;
import org.example.response.DialogResponse;
import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Команда /start: начинает диалог заново - сбрасывает город и просит выбрать новый
 */
public class StartHandler implements ButtonHandler {
    /**
     * Приветственное сообщение
     */
    private static final String START_MESSAGE = "Привет, я бот — помощник в путешествиях." +
            " В какой город вы хотели бы отправиться?";

    @Override
    public DialogResponse execute(BotSession session) {
        session.setState(DialogState.WAITING_FOR_CITY);
        session.setCity(null);
        return new DialogResponse(START_MESSAGE);
    }
}
