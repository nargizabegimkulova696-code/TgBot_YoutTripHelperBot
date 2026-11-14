package org.example.service;

import org.example.command.places.PlacesProvider;
import org.example.command.places.PlacesProvider.Place;
import org.example.paging.Page;

import java.io.IOException;


/**
 * Сервис доступа к местам (еда, культура, развлечения):
 * запрашивает у источника мест список мест города по тегу и делит его на страницы
 */
public class TripService {
    /**
     * Сколько мест на одной странице
     */
    public static final int PAGE_SIZE = 5;
    /**
     * Источник мест, из которого сервис получает полный список мест города по тегу
     */
    private final PlacesProvider provider;

    public TripService(PlacesProvider provider) {
        this.provider = provider;
    }

    /**
     * Находит места города по тегу и возвращает одну страницу этого списка
     *
     * @param city   город, в котором ищем места
     * @param tag    тег, например "tag:cafe"
     * @param number номер страницы, начиная с 0
     * @return страница мест, пустая, если по тегу ничего не нашлось
     * @throws IOException если источник мест не смог вернуть данные (нет сети, ошибка API, город не найден)
     */
    public Page<Place> getPage(String city, String tag, int number) throws IOException {
        return new Page<>(provider.getPlaces(city, tag), number, PAGE_SIZE);
    }
}
