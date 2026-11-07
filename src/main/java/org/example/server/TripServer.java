package org.example.server;

import org.example.command.places.Place;
import org.example.command.places.PlacesProvider;
import org.example.paging.Page;
import org.example.storage.UserPlace;
import org.example.storage.UserPlaceStorage;

import java.io.IOException;
import java.sql.SQLException;
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
    /**
     * Хранилище мест, добавленных пользователями
     */
    private final UserPlaceStorage storage;

    public TripServer(PlacesProvider provider, UserPlaceStorage storage) {
        this.provider = provider;
        this.storage = storage;
    }

    /**
     * Все места по тегу: сначала места из API, потом места, добавленные этим пользователем
     *
     * @param chatId идентификатор чата пользователя
     * @param city   город
     * @param tag    тег, например "tag:cafe"
     * @return общий список мест
     * @throws IOException  если места из API получить не удалось
     * @throws SQLException ошибка работы с базой
     */
    private List<Place> getAllPlaces(long chatId, String city, String tag) throws IOException, SQLException {
        // копия, чтобы не менять общий список источника мест
        List<Place> all = new ArrayList<>(provider.getPlaces(city, tag));
        all.addAll(storage.getPlaces(chatId, city, tag));
        return all;
    }

    /**
     * Страница мест по тегу
     *
     * @param chatId идентификатор чата пользователя
     * @param city   город, в котором ищем места
     * @param tag    тег, например "tag:cafe"
     * @param number номер страницы, начиная с 0
     * @return страница мест или null, если места получить не удалось
     */
    public Page<Place> getPage(long chatId, String city, String tag, int number) {
        try {
            return new Page<>(getAllPlaces(chatId, city, tag), number, PAGE_SIZE);
        } catch (IOException | SQLException e) {
            return null;
        }
    }

    /**
     * Место пользователя по номеру в общем списке (нумерация с 1)
     *
     * @param chatId идентификатор чата пользователя
     * @param city   город
     * @param tag    тег, например "tag:cafe"
     * @param number номер места в общем списке
     * @return место или null, если номер относится к месту из API или такого номера нет
     * @throws IOException  если места из API получить не удалось
     * @throws SQLException ошибка работы с базой
     */
    public UserPlace findUserPlace(long chatId, String city, String tag, int number) throws IOException, SQLException {
        List<Place> all = getAllPlaces(chatId, city, tag);
        if (number < 1 || number > all.size()) {
            return null;
        }
        // дополнительная проверка, является ли это нужным нам местом пользователя
        if (all.get(number - 1) instanceof UserPlace own) {
            return own;
        }
        return null;
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
