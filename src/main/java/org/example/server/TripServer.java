package org.example.server;

import org.example.catalog.tags.TagsCatalog;
import org.example.session.DialogState;

/**
 * Сервер по состоянию, в котором находится пользователь
 * после выбора одной из категорий: food, culture, attractions,
 * обрабатывает тег, выбранный пользователем
 */
public class TripServer {
    /**
     * Каталог категории food
     */
    private final TagsCatalog food;

    /**
     * Каталог категории culture
     */
    private final TagsCatalog culture;

    /**
     * Каталог категории attractions
     */
    private final TagsCatalog attractions;

    /**
     * Ответное сообщение, если мест нет
     */
    private final static String NOT_FOUND_TAG = "Места для тега не найдены(";

    /**
     * Инициализация сервера и каталогов
     */
    public TripServer(TagsCatalog food, TagsCatalog culture, TagsCatalog attractions) {
        this.food = food;
        this.culture = culture;
        this.attractions = attractions;
    }

    /**
     * Возврат списка мест по тегу, либо его отсутствие
     *
     * @param state - по состоянию определяется каталог, в котором ищем тег
     * @param city  - город, по которому определяется список мест
     * @param tag   - тег, для которого ищем список мест
     */
    public String getPlaces(DialogState state, String city, String tag) {

        switch (state) {

            case DialogState.CAT_FOOD -> {

                if (food.getPlaces(city, tag) == null) {
                    return NOT_FOUND_TAG;
                }

                return food.getPlaces(city, tag);
            }

            case DialogState.CAT_CULTURE -> {

                if (culture.getPlaces(city, tag) == null) {
                    return NOT_FOUND_TAG;
                }

                return culture.getPlaces(city, tag);
            }

            default -> {
                if (attractions.getPlaces(city, tag) == null) {
                    return NOT_FOUND_TAG;
                }

                return attractions.getPlaces(city, tag);
            }
        }
    }

    ;
}
