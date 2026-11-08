package org.example.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Хранилище мест, добавленных пользователями, в базе данных SQLite.
 */
public class UserPlaceStorage implements AutoCloseable {
    /**
     * Соединение с базой данных
     */
    private final Connection connection;

    /**
     * Открывает базу по адресу и создаёт таблицу, если её ещё нет
     *
     * @param url адрес базы
     * @throws IllegalStateException если базу открыть или создать не удалось
     */
    public UserPlaceStorage(String url) {
        try {
            connection = DriverManager.getConnection(url);
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS user_places (
                            id       INTEGER PRIMARY KEY AUTOINCREMENT,
                            chat_id  INTEGER NOT NULL,
                            city     TEXT NOT NULL,
                            category TEXT NOT NULL,
                            tag      TEXT NOT NULL,
                            name     TEXT NOT NULL,
                            address  TEXT NOT NULL
                        )
                        """);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Не удалось открыть базу данных " + url, e);
        }
    }


    /**
     * Места пользователя в городе и теге, в порядке добавления
     *
     * @param chatId идентификатор чата
     * @param city   город
     * @param tag    тег
     * @return список мест, пустой если ничего не добавлено
     * @throws SQLException ошибка работы с базой
     */
    public synchronized List<UserPlace> getPlaces(long chatId, String city, String tag) throws SQLException {
        String sql = "SELECT id, category, tag, name, address FROM user_places "
                + "WHERE chat_id = ? AND city = ? AND tag = ? ORDER BY id";
        List<UserPlace> places = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, chatId);
            statement.setString(2, city);
            statement.setString(3, tag);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    places.add(new UserPlace(rows.getLong("id"),
                            rows.getString("category"),
                            rows.getString("tag"),
                            rows.getString("name"),
                            rows.getString("address")));
                }
            }
        }
        return places;
    }

    /**
     * Добавляет место пользователя (add_place)
     *
     * @param chatId   идентификатор чата
     * @param city     город
     * @param category категория: "food", "culture" или "attractions"
     * @param tag      тег, в который добавлено место, например "tag:cafe"
     * @param name     название места
     * @param address  адрес места
     * @throws SQLException ошибка работы с базой
     */
    public synchronized void add(long chatId, String city, String category, String tag,
                                 String name, String address) throws SQLException {
        String sql = "INSERT INTO user_places (chat_id, city, category, tag, name, address) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, chatId);
            statement.setString(2, city);
            statement.setString(3, category);
            statement.setString(4, tag);
            statement.setString(5, name);
            statement.setString(6, address);
            statement.executeUpdate();
        }
    }

    /**
     * Меняет название и адрес места, если оно принадлежит этому пользователю (edit_place)
     *
     * @param chatId  идентификатор чата
     * @param id      идентификатор места
     * @param name    новое название
     * @param address новый адрес
     * @return true если место найдено и изменено, false если его нет или оно чужое
     * @throws SQLException ошибка работы с базой
     */
    public synchronized boolean update(long chatId, long id, String name, String address) throws SQLException {
        String sql = "UPDATE user_places SET name = ?, address = ? WHERE id = ? AND chat_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, address);
            statement.setLong(3, id);
            statement.setLong(4, chatId);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Удаляет место, если оно принадлежит этому пользователю (delete_place)
     *
     * @param chatId идентификатор чата владельца
     * @param id     идентификатор места
     * @return true если место удалено, false если его нет или оно чужое
     * @throws SQLException ошибка работы с базой
     */
    public synchronized boolean delete(long chatId, long id) throws SQLException {
        String sql = "DELETE FROM user_places WHERE id = ? AND chat_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, chatId);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Закрывает соединение с базой
     *
     * @throws SQLException ошибка при закрытии
     */
    @Override
    public void close() throws SQLException {
        connection.close();
    }
}