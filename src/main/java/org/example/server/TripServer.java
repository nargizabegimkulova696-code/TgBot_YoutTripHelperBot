package org.example.server;

import org.example.command.places.Place;
import org.example.command.places.PlacesProvider;
import org.example.paging.Page;
import org.example.storage.places.UserPlace;
import org.example.storage.places.UserPlaceRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервер, предназначенный для обработки тега пользователя,
 * который выбран внутри одной из категорий: еда, культура, развлечения
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
     * Хранилище мест, добавленных пользователями.
     */
    private final UserPlaceRepository storage;

    /**
     * Создаёт сервис для работы с местами городского каталога и местами пользователей.
     *
     * @param provider источник мест городского каталога
     * @param storage  хранилище мест, добавленных пользователями
     */
    public TripServer(PlacesProvider provider, UserPlaceRepository storage) {
        this.provider = provider;
        this.storage = storage;
    }

    /**
     * Метод добавляет место в бд
     *
     * @param chatId   - ID пользователя
     * @param city     - текущий город
     * @param category - текущая категория
     * @param tag      - текущий тег
     * @param name     - название места
     * @param address  - адрес места
     */
    public boolean addPlace(long chatId, String city, String category, String tag,
                            String name, String address) {
        try {
            storage.add(chatId, city, category, tag, name, address);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Метод меняет название или адрес места
     *
     * @param chatId  - ID пользователя
     * @param id      - ID места в бд
     * @param name    - название места
     * @param address - адрес места
     */
    public boolean editPlace(long chatId, long id, String name, String address) {
        try {
            return storage.update(chatId, id, name, address);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Метод удаляет место из списка мест
     *
     * @param chatId - ID пользователя
     * @param id     - ID места в бд
     */
    public boolean deletePlace(long chatId, long id) {
        try {
            return storage.delete(chatId, id);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Проверка доступны ли пользователю кнопки edit_place и delete_place
     *
     * @param chat_id - ID пользователя
     * @param city    - название города
     * @param tag     - текущий тег
     */
    public boolean hasUserPlaces(long chat_id, String city, String tag) {
        try {
            List<UserPlace> places = storage.getPlaces(chat_id, city, tag);
            return !places.isEmpty();
        } catch (SQLException e) {
            return false;
        }

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
     * Нумерация сквозная по всем страницам, начиная с 1
     */
    public String formatted(Page<Place> page) {
        List<String> blocks = new ArrayList<>();
        List<Place> items = page.getItems();

        for (int i = 0; i < items.size(); i++) {
            int number = page.getFirstIndex() + i + 1;
            blocks.add(number + ") " + items.get(i).format());
        }

        return String.join("\n\n", blocks);
    }
}
