package org.example.response;

import org.example.handler.command.*;
import org.example.paging.Page;
import org.example.paging.PageCallback;
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
                add(keyboard, "о городе", AboutCityHandler.CALLBACK);
                add(keyboard, "погода", WeatherHandler.CALLBACK);
                add(keyboard, "еда", FoodHandler.CALLBACK);
                add(keyboard, "культура", CultureHandler.CALLBACK);
                add(keyboard, "развлечения", AttractionsHandler.CALLBACK);
            }

            case CAT_ABOUT_CITY -> {
                add(keyboard, "погода", WeatherHandler.CALLBACK);
                add(keyboard, "еда", FoodHandler.CALLBACK);
                add(keyboard, "культура", CultureHandler.CALLBACK);
                add(keyboard, "развлечения", AttractionsHandler.CALLBACK);
            }

            case CAT_WEATHER -> {
                add(keyboard, "о городе", AboutCityHandler.CALLBACK);
                add(keyboard, "еда", FoodHandler.CALLBACK);
                add(keyboard, "культура", CultureHandler.CALLBACK);
                add(keyboard, "развлечения", AttractionsHandler.CALLBACK);
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

        add(keyboard, "назад", BackHandler.CALLBACK);
        return keyboard;
    }

    /**
     * Кнопки под страницей мест: "ещё" - если есть следующая страница,
     * "назад" - на предыдущую страницу, а с первой страницы - к выбору тега
     *
     * @param tag  тег, места которого показаны
     * @param page показанная страница
     */
    public List<List<Button>> buildPage(String tag, Page<?> page) {
        List<List<Button>> keyboard = new ArrayList<>();

        if (page.hasNext()) {
            add(keyboard, "далее", new PageCallback(tag, page.getNumber() + 1).toData());
        }

        if (page.hasPrevious()) {
            add(keyboard, "назад", new PageCallback(tag, page.getNumber() - 1).toData());
        } else {
            add(keyboard, "назад", BackHandler.CALLBACK);
        }

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
