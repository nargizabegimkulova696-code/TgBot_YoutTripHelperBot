package org.example.response;

import org.example.session.DialogState;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс инициализирует список категорий, доступных пользователю после выбора города
 */
public class KeyboardFactory {
    /**
     * Метод создает список, состоящий из доступных пользователю категорий
     * @param state - по состоянию пользователя определяется какой список категорий ему доступен на текущем
     *              уровне диалога с ботом
     */
    public List<List<Button>> build(DialogState state) {
        List<List<Button>> keyboard = new ArrayList<>();

        switch (state) {
            case WAITING_FOR_CITY -> {
                return keyboard;
            }
            case CITY_SELECTED -> {
                add(keyboard, "about city", "cat:about_city");
                add(keyboard, "weather", "cat:weather");
                add(keyboard, "food", "cat:food");
                add(keyboard, "culture", "cat:culture");
                add(keyboard, "attractions", "cat:attractions");
            }

            case CAT_ABOUT_CITY -> {
                add(keyboard, "weather", "cat:weather");
                add(keyboard, "food", "cat:food");
                add(keyboard, "culture", "cat:culture");
                add(keyboard, "attractions", "cat:attractions");
            }

            case CAT_WEATHER -> {
                add(keyboard, "about_city", "cat:about_city");
                add(keyboard, "food", "cat:food");
                add(keyboard, "culture", "cat:culture");
                add(keyboard, "attractions", "cat:attractions");
            }

            case CAT_FOOD -> {
                add(keyboard, "рестораны", "tag:restaurants");
                add(keyboard, "кафе", "tag:cafe");
                add(keyboard, "пекарни", "tag:bakeries");
                add(keyboard, "пиццерии", "tag:pizzerias");
                add(keyboard, "фастфуд", "tag:fastfood");
            }

            case CAT_CULTURE -> {
                add(keyboard, "музеи", "tag:museums");
                add(keyboard, "храмы", "tag:temples");
                add(keyboard, "исторические здания", "tag:historical_buildings");
                add(keyboard, "памятники", "tag:monuments");
                add(keyboard, "театры", "tag:theaters");
            }

            case CAT_ATTRACTIONS -> {
                add(keyboard, "ТРЦ", "tag:shopping");
                add(keyboard, "парки аттракционов", "tag:attraction_parks");
                add(keyboard, "кино", "tag:movie");
                add(keyboard, "зоопарки", "tag:zoos");
                add(keyboard, "природа и виды", "tag:nature");
            }
        }

        add(keyboard, "back", "cat:back");
        return keyboard;
    }

    /**
     * Добавление кнопок в список кнопок
     */
    private void add(List<List<Button>> keyboard, String nameOfCat, String callback) {
        List<Button> button = new ArrayList<>();
        button.add(new Button(nameOfCat, callback));
        keyboard.add(button);
    }
}
