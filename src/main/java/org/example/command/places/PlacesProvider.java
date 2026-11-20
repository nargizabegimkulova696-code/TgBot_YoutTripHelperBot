package org.example.command.places;

import java.io.IOException;
import java.util.List;

/**
 * Источник мест для категорий food, culture, attractions.
 * Логика бота работает только с этим интерфейсом, поэтому источник (API, файл, БД) можно заменить
 */
public interface PlacesProvider {

    /**
     * Список мест в городе по тегу
     *
     * @param city название города
     * @param tag  тег из callback кнопки, например "tag:cafe"
     * @return список мест
     * @throws IOException если места получить не удалось
     */
    List<Place> getPlaces(String city, String tag) throws IOException;
}
