package org.example.CityResponse;

import java.util.ArrayList;
import java.util.List;

public class DialogResponse {
    private final String textMessage;
    private final List<List<Button>> listOfCommands = new ArrayList<>();

    public DialogResponse(String text) {
        textMessage = text;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public List<List<Button>> getListOfCommands() {
        return listOfCommands;
    }

    public void addButton(List<Button> button) {
        listOfCommands.add(button);
    }

    public boolean hasButtons() {
        return !listOfCommands.isEmpty();
    }
}
