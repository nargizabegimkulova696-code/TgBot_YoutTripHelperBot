package org.example.storage.favourites;

import org.example.command.places.Place;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Хранилище избранных мест пользователей в SQLite
 */
public class UserFavouriteStorage implements UserFavouriteRepository {

    /**
     * JDBC-адрес базы данных
     */
    private final String url;

    /**
     * Создаёт таблицу избранного, если её ещё нет
     *
     * @param url JDBC-адрес базы данных
     */
    public UserFavouriteStorage(String url) {
        this.url = url;

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS user_favourites (
                        id       INTEGER PRIMARY KEY AUTOINCREMENT,
                        chat_id  INTEGER NOT NULL,
                        city     TEXT NOT NULL,
                        category TEXT NOT NULL,
                        name     TEXT NOT NULL,
                        address  TEXT NOT NULL,
                        UNIQUE (chat_id, category, city, name, address)
                    )
                    """);
        } catch (SQLException e) {
            throw new IllegalStateException("Не удалось создать таблицу избранного", e);
        }
    }

    @Override
    public boolean addFavourite(long chatId, String city, String category, Place place)
            throws SQLException {
        String sql = """
                INSERT INTO user_favourites (chat_id, city, category, name, address)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (chat_id, category, city, name, address) DO NOTHING
                """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, chatId);
            statement.setString(2, city);
            statement.setString(3, category);
            statement.setString(4, place.getName());
            statement.setString(5, place.getAddress());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public List<FavouritePlace> getFavourites(long chatId, String category) throws SQLException {
        String sql = """
                SELECT id, city, category, name, address
                FROM user_favourites
                WHERE chat_id = ? AND category = ?
                ORDER BY id
                """;

        List<FavouritePlace> places = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, chatId);
            statement.setString(2, category);

            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    places.add(new FavouritePlace(
                            rows.getLong("id"),
                            rows.getString("city"),
                            rows.getString("category"),
                            rows.getString("name"),
                            rows.getString("address")));
                }
            }
        }
        return places;
    }

    @Override
    public boolean deleteFavourite(long chatId, long favouriteId) throws SQLException {
        String sql = "DELETE FROM user_favourites WHERE chat_id = ? AND id = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, chatId);
            statement.setLong(2, favouriteId);
            return statement.executeUpdate() > 0;
        }
    }
}