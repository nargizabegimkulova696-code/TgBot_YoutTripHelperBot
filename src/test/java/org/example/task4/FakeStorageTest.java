package org.example.task4;

import org.example.storage.UserPlace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Проверяет выборку пользовательских мест и права владельца в FakeStorage.
 */
class FakeStorageTest {

    /**
     * Хранилище мест для текущего теста.
     */
    private FakeStorage storage;

    /**
     * Возвращает названия мест в порядке их следования в списке.
     *
     * @param places места, полученные из хранилища
     * @return список названий
     */
    private List<String> namesOf(List<UserPlace> places) {
        return places.stream().map(UserPlace::getName).toList();
    }

    /**
     * Добавляет места двух чатов в одном городе и теге.
     * У первого чата также есть место в другом городе и место в другом теге.
     */
    @BeforeEach
    void setUp() {
        storage = new FakeStorage();
        storage.add(1L, "Казань", "food", "tag:cafe", "Кафе 1", "ул. Ленина, 1");
        storage.add(2L, "Казань", "food", "tag:cafe", "Кафе 2", "ул. Ленина, 2");
        storage.add(1L, "Москва", "food", "tag:cafe", "Кафе 3", "ул. Ленина, 3");
        storage.add(1L, "Казань", "food", "tag:bakeries", "Пекарня 1", "ул. Ленина, 4");
        storage.add(1L, "Казань", "food", "tag:cafe", "Кафе 4", "ул. Ленина, 5");
    }

    /**
     * Каждый чат видит свои места в выбранном городе и теге в порядке добавления.
     */
    @Test
    void testGetPlacesByChatCityAndTag() {
        assertEquals(List.of("Кафе 1", "Кафе 4"), namesOf(storage.getPlaces(1L, "Казань", "tag:cafe")));
        assertEquals(List.of("Кафе 2"), namesOf(storage.getPlaces(2L, "Казань", "tag:cafe")));
        assertEquals(List.of("Кафе 3"), namesOf(storage.getPlaces(1L, "Москва", "tag:cafe")));
        assertEquals(List.of("Пекарня 1"), namesOf(storage.getPlaces(1L, "Казань", "tag:bakeries")));
    }

    /**
     * Новый чат получает пустой список, даже если другие чаты
     * добавили места в тот же город и тег.
     */
    @Test
    void testGetPlacesForNewChat() {
        assertEquals(List.of(), storage.getPlaces(30L, "Казань", "tag:cafe"));
    }

    /**
     * Попытка изменить место другого чата возвращает false.
     * У владельца сохраняются исходные название и адрес.
     */
    @Test
    void testUpdateAnotherChatPlace() {
        long placeId = storage.getPlaces(1L, "Казань", "tag:cafe").get(0).getId();
        boolean updated = storage.update(2L, placeId, "Кафе 5", "ул. Мира, 10");

        assertFalse(updated);
        UserPlace saved = storage.getPlaces(1L, "Казань", "tag:cafe").get(0);
        assertEquals("Кафе 1", saved.getName());
        assertEquals("ул. Ленина, 1", saved.getAddress());
        assertEquals(List.of("Кафе 2"), namesOf(storage.getPlaces(2L, "Казань", "tag:cafe")));
    }

    /**
     * Владелец может изменить название и адрес своего места.
     * Идентификатор и позиция места в списке сохраняются.
     */
    @Test
    void testUpdateOwnPlace() {
        long placeId = storage.getPlaces(1L, "Казань", "tag:cafe").get(0).getId();
        boolean updated = storage.update(1L, placeId, "Кафе 5", "ул. Мира, 10");

        assertTrue(updated);
        List<UserPlace> saved = storage.getPlaces(1L, "Казань", "tag:cafe");
        assertEquals(List.of("Кафе 5", "Кафе 4"), namesOf(saved));
        assertEquals(placeId, saved.get(0).getId());
        assertEquals("ул. Мира, 10", saved.get(0).getAddress());
    }

    /**
     * Попытка изменить удалённое место возвращает false.
     * Оставшееся место сохраняет своё название и адрес.
     */
    @Test
    void testUpdateDeletedPlace() {
        long placeId = storage.getPlaces(1L, "Казань", "tag:cafe").get(0).getId();
        storage.delete(1L, placeId);
        boolean updated = storage.update(1L, placeId, "Кафе 5", "ул. Мира, 10");

        assertFalse(updated);
        List<UserPlace> saved = storage.getPlaces(1L, "Казань", "tag:cafe");
        assertEquals(List.of("Кафе 4"), namesOf(saved));
        assertEquals("ул. Ленина, 5", saved.get(0).getAddress());
    }

    /**
     * Попытка удалить место другого чата возвращает false.
     * Оба чата сохраняют свои списки мест.
     */
    @Test
    void testDeleteAnotherChatPlace() {
        long placeId = storage.getPlaces(1L, "Казань", "tag:cafe").get(0).getId();
        boolean deleted = storage.delete(2L, placeId);

        assertFalse(deleted);
        assertEquals(List.of("Кафе 1", "Кафе 4"), namesOf(storage.getPlaces(1L, "Казань", "tag:cafe")));
        assertEquals(List.of("Кафе 2"), namesOf(storage.getPlaces(2L, "Казань", "tag:cafe")));
    }

    /**
     * Владелец может удалить выбранное место. Остальные места сохраняются,
     * а повторное удаление по тому же идентификатору возвращает false.
     */
    @Test
    void testDeleteOwnPlace() {
        long placeId = storage.getPlaces(1L, "Казань", "tag:cafe").get(0).getId();
        boolean deleted = storage.delete(1L, placeId);

        assertTrue(deleted);
        assertEquals(List.of("Кафе 4"), namesOf(storage.getPlaces(1L, "Казань", "tag:cafe")));
        assertEquals(List.of("Кафе 2"), namesOf(storage.getPlaces(2L, "Казань", "tag:cafe")));
        assertFalse(storage.delete(1L, placeId));
    }

    /**
     * Изменения в полученном объекте остаются черновиком до вызова update.
     * Повторное чтение возвращает сохранённые название и адрес.
     */
    @Test
    void testEditPlaceWithoutSaving() {
        UserPlace draft = storage.getPlaces(1L, "Казань", "tag:cafe").get(0);

        draft.setName("Кафе 5");
        draft.setAddress("ул. Мира, 10");

        UserPlace saved = storage.getPlaces(1L, "Казань", "tag:cafe").get(0);
        assertEquals("Кафе 1", saved.getName());
        assertEquals("ул. Ленина, 1", saved.getAddress());
    }


}