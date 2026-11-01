package org.example.server;

import org.example.command.places.PlacesProvider;
import org.example.command.places.PlacesProvider.Place;
import org.example.paging.Page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервер по состоянию, в котором находится пользователь
 * после выбора одной из категорий: food, culture, attractions,
 * обрабатывает тег, выбранный пользователем
 */
public class TripServer {
    /**
     * Сколько мест на одной странице
     */
    public static final int PAGE_SIZE = 5;
    /**
     * Источник мест
     */
    private final PlacesProvider provider;

    public TripServer(PlacesProvider provider) {
        this.provider = provider;
    }

    /**
     * Страница мест по тегу
     *
     * @param city   город, в котором ищем места
     * @param tag    тег, например "tag:cafe"
     * @param number номер страницы, начиная с 0
     * @return страница мест или null, если места получить не удалось
     */
    public Page<Place> getPage(String city, String tag, int number) {
        try {
            return new Page<>(provider.getPlaces(city, tag), number, PAGE_SIZE);
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * Форматирует места страницы в текст сообщения
     */
    public String formatted(Page<Place> page) {
        List<String> blocks = new ArrayList<>();

        for (Place place : page.getItems()) {
            blocks.add("- " + place.format());
        }

        return String.join("\n\n", blocks);
    }
}
