package org.example.DialogService;

import org.example.Session.DialogState;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {
    public List<List<Button>> build(DialogState state) {
        List<List<Button>> newKeyboard = new ArrayList<>();

        switch (state) {
            case WAITING_FOR_CITY -> {
            }
            case CITY_SELECTED -> {
                List<Button> rowOfButton1 = new ArrayList<>();
                rowOfButton1.add(new Button("about city", "cat:about_city"));

                List<Button> rowOfButton2 = new ArrayList<>();
                rowOfButton2.add(new Button("weather", "cat:about_city"));

                List<Button> rowOfButton3 = new ArrayList<>();
                rowOfButton3.add(new Button("back", "cat:back"));

                newKeyboard.add(rowOfButton1);
                newKeyboard.add(rowOfButton2);
                newKeyboard.add(rowOfButton3);
            }

            case CAT_ABOUT_CITY -> {
                List<Button> rowOfButton1 = new ArrayList<>();
                rowOfButton1.add(new Button("weather", "cat:weather"));

                List<Button> rowOfButton2 = new ArrayList<>();
                rowOfButton2.add(new Button("back", "cat:back"));

                newKeyboard.add(rowOfButton1);
                newKeyboard.add(rowOfButton2);
            }

            case CAT_WEATHER -> {
                List<Button> rowOfButton1 = new ArrayList<>();
                rowOfButton1.add(new Button("about_city", "cat:about_city"));

                List<Button> rowOfButton2 = new ArrayList<>();
                rowOfButton2.add(new Button("back", "cat:back"));

                newKeyboard.add(rowOfButton1);
                newKeyboard.add(rowOfButton2);
            }
        }

        return newKeyboard;
    }

}
