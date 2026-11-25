package org.example.storage.places;

import java.sql.SQLException;
import java.util.List;


/**
 * Хранилище мест, которые пользователи добавили к спискам мест в городах.
 */
public interface UserPlaceRepository {
    /**
     * Места пользователя в городе и теге, в порядке добавления.
     *
     * @param chatId идентификатор чата
     * @param city   город
     * @param tag    тег
     * @return список мест, пустой, если ничего не добавлено
     * @throws SQLException ошибка работы с хранилищем
     */
    List<UserPlace> getPlaces(long chatId, String city, String tag) throws SQLException;

    /**
     * Добавляет место пользователя (add_place).
     *
     * @param chatId   идентификатор чата
     * @param city     город
     * @param category категория: "food", "culture" или "attractions"
     * @param tag      тег, в который добавлено место, например "tag:cafe"
     * @param name     название места
     * @param address  адрес места
     * @throws SQLException ошибка работы с хранилищем
     */
    void add(long chatId, String city, String category, String tag, String name, String address) throws SQLException;

    /**
     * Меняет название и адрес места, если оно принадлежит этому пользователю (edit_place).
     *
     * @param chatId  идентификатор чата
     * @param id      идентификатор места
     * @param name    новое название
     * @param address новый адрес
     * @return {@code true}, если место найдено и изменено,
     * {@code false}, если его нет или оно чужое
     * @throws SQLException ошибка работы с хранилищем
     */
    boolean update(long chatId, long id, String name, String address) throws SQLException;

    /**
     * Удаляет место, если оно принадлежит этому пользователю (delete_place).
     *
     * @param chatId идентификатор чата владельца
     * @param id     идентификатор места
     * @return {@code true}, если место удалено,
     * {@code false}, если его нет или оно чужое
     * @throws SQLException ошибка работы с хранилищем
     */
    boolean delete(long chatId, long id) throws SQLException;
}
