package org.example.task5;

import org.example.command.places.Place;
import org.example.storage.FavouritePlace;
import org.example.storage.UserFavouriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Проверяет контракт тестового хранилища избранных мест
 */
class FakeFavouriteStorageTest {

    /**
     * Хранилище избранных мест для текущего теста
     */
    private UserFavouriteRepository storage;

    /**
     * Создаёт хранилище с местами двух чатов в разных городах и категориях
     */
    @BeforeEach
    void setUp() throws SQLException {
        storage = new FakeFavouriteStorage();
        storage.addFavourite(1L, "Казань", "food", new Place("Кафе 1", "ул. Ленина, 1"));
        storage.addFavourite(2L, "Казань", "food", new Place("Кафе 2", "ул. Ленина, 2"));
        storage.addFavourite(1L, "Казань", "culture", new Place("Музей", "ул. Мира, 3"));
        storage.addFavourite(1L, "Москва", "food", new Place("Кафе 3", "ул. Мира, 4"));
    }

    /**
     * Возвращает названия избранных мест в порядке выдачи
     */
    private List<String> namesOf(List<FavouritePlace> places) {
        return places.stream().map(FavouritePlace::getName).toList();
    }

    /**
     * Добавление сохраняет город, категорию, название и адрес места
     */
    @Test
    void testAddFavourite() throws SQLException {
        boolean added = storage.addFavourite(1L, "Москва", "attractions",
                new Place("Парк", "ул. Мира, 5"));

        assertTrue(added);
        List<FavouritePlace> saved = storage.getFavourites(1L, "attractions");
        assertEquals(1, saved.size());
        FavouritePlace place = saved.getFirst();
        assertEquals("Москва", place.getCity());
        assertEquals("attractions", place.getCategory());
        assertEquals("Парк", place.getName());
        assertEquals("ул. Мира, 5", place.getAddress());
    }

    /**
     * Повторное добавление того же места возвращает false и сохраняет исходный список
     */
    @Test
    void testRejectDuplicateFavourite() throws SQLException {
        boolean added = storage.addFavourite(1L, "Казань", "food",
                new Place("Кафе 1", "ул. Ленина, 1"));

        assertFalse(added);
        assertEquals(List.of("Кафе 1", "Кафе 3"), namesOf(storage.getFavourites(1L, "food")));
    }

    /**
     * Одинаковые название и адрес допустимы в другом городе, категории или чате
     */
    @Test
    void testAllowSamePlaceInDifferentContexts() throws SQLException {
        Place place = new Place("Кафе 1", "ул. Ленина, 1");

        assertTrue(storage.addFavourite(1L, "Москва", "food", place));
        assertTrue(storage.addFavourite(1L, "Казань", "culture", place));
        assertTrue(storage.addFavourite(2L, "Казань", "food", place));

        List<FavouritePlace> food = storage.getFavourites(1L, "food");
        assertEquals(List.of("Кафе 1", "Кафе 3", "Кафе 1"), namesOf(food));
        assertEquals("Москва", food.getLast().getCity());
        assertEquals(List.of("Музей", "Кафе 1"), namesOf(storage.getFavourites(1L, "culture")));
        assertEquals(List.of("Кафе 2", "Кафе 1"), namesOf(storage.getFavourites(2L, "food")));
    }

    /**
     * Выборка возвращает места нужного чата и категории в порядке добавления
     */
    @Test
    void testGetFavouritesByChatAndCategory() throws SQLException {
        assertEquals(List.of("Кафе 1", "Кафе 3"), namesOf(storage.getFavourites(1L, "food")));
        assertEquals(List.of("Музей"), namesOf(storage.getFavourites(1L, "culture")));
        assertEquals(List.of("Кафе 2"), namesOf(storage.getFavourites(2L, "food")));
        assertTrue(storage.getFavourites(3L, "food").isEmpty());
        assertTrue(storage.getFavourites(1L, "attractions").isEmpty());
    }

    /**
     * Попытка удалить чужую запись возвращает false и сохраняет списки обоих чатов
     */
    @Test
    void testDeleteAnotherChatFavourite() throws SQLException {
        long favouriteId = storage.getFavourites(1L, "food").getFirst().getId();

        assertFalse(storage.deleteFavourite(2L, favouriteId));
        assertEquals(List.of("Кафе 1", "Кафе 3"), namesOf(storage.getFavourites(1L, "food")));
        assertEquals(List.of("Кафе 2"), namesOf(storage.getFavourites(2L, "food")));
    }

    /**
     * Владелец удаляет свою запись, а повторное удаление возвращает false
     */
    @Test
    void testDeleteOwnFavourite() throws SQLException {
        long favouriteId = storage.getFavourites(1L, "food").getFirst().getId();

        assertTrue(storage.deleteFavourite(1L, favouriteId));
        assertEquals(List.of("Кафе 3"), namesOf(storage.getFavourites(1L, "food")));
        assertEquals(List.of("Кафе 2"), namesOf(storage.getFavourites(2L, "food")));
        assertFalse(storage.deleteFavourite(1L, favouriteId));
    }
}