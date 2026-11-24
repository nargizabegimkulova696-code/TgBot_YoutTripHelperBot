package org.example.storage;

import org.example.command.places.Place;

import java.sql.SQLException;
import java.util.List;

/**
 * Хранилище избранных мест пользователей
 */
public interface UserFavouriteRepository {
    /**
     * Добавляет место в избранном пользователя
     *
     * @return {@code true} если место добавлено,
     * {@code false} если оно уже есть в избранном этой категории
     * @throws SQLException ошибка работы с хранилищем
     */
    boolean addFavourite(long chatId, String city, String category, Place place) throws SQLException;

    /**
     * Возвращает избранные места пользователя в категории, в порядке добавления
     *
     * @return список мест, пустой, если ничего не добавлено
     * @throws SQLException ошибка работы с хранилищем
     */
    List<FavouritePlace> getFavourites(long chatId, String category) throws SQLException;

    /**
     * Удаляет запись из избранного пользователя
     *
     * @return {@code true} если запись удалена,
     * {@code false} если её нет или она чужая
     * @throws SQLException ошибка работы с хранилищем
     */
    boolean deleteFavourite(long chatId, long favouriteId) throws SQLException;
}