package org.example.CityResponse;

public class Button {
    private final String command;
    private final String callBackData;

    public Button(String commandText, String callBackData) {
        command = commandText;
        this.callBackData = callBackData;
    }

    public String getCommandText() {
        return command;
    }

    public String getCallBackData() {
        return callBackData;
    }
}
