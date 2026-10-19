package org.example;

import org.example.DialogService.DialogResponse;
import org.example.DialogService.KeyboardFactory;
import org.example.Session.BotSession;
import org.example.Session.DialogState;

public class BackCommand {
    private static final String START_MESSAGE = "Привет, я бот — помощник в путешествиях. В какой город вы хотели бы отправиться?";

    public DialogResponse back(BotSession session) {
        DialogState state = session.getState();
        DialogResponse newResponse;
        KeyboardFactory keyboard = new KeyboardFactory();

        switch (state) {
            case WAITING_FOR_CITY -> {
                newResponse = new DialogResponse(START_MESSAGE);
            }

            case CITY_SELECTED -> {
                session.setState(DialogState.WAITING_FOR_CITY);
                newResponse = new DialogResponse(START_MESSAGE, keyboard.build(session.getState()));
            }

            default -> {
                session.setState(DialogState.CITY_SELECTED);
                newResponse = new DialogResponse("Выберете категорию для города " + session.getCity() + ":",
                        keyboard.build(session.getState()));
            }

        }

        return newResponse;
    }
}
