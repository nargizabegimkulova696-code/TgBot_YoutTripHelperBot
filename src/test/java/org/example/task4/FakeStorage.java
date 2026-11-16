package org.example.task4;

import org.example.storage.UserPlace;
import org.example.storage.UserPlaceStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Фейковая база данных для тестов команд: add_place, edit_place, delete_place
 */
public class FakeStorage extends UserPlaceStorage {
    /**
     * ID места
     */
    private Long nextId = 0L;

    /**
     * Фейковая база данных
     */
    private final Map<Long, ArrayList<UserPlace>> table = new ConcurrentHashMap<>();

    /**
     * Конструктор заглушка для родителя
     */
    public FakeStorage() {
        super("jdbc:sqlite::memory:");
    }

    @Override
    public List<UserPlace> getPlaces(long chatId, String city, String tag) {
        List<UserPlace> result = new ArrayList<>();
        for (UserPlace p : table.computeIfAbsent(chatId, k -> new ArrayList<>())) {
            result.add(new UserPlace(p.getId(), p.getCategory(), p.getTag(), p.getName(), p.getAddress()));
        }
        return result;
    }

    @Override
    public void add(long chatId, String city, String category, String tag,
                    String name, String address) {
        ArrayList<UserPlace> places = table.computeIfAbsent(chatId, array -> new ArrayList<UserPlace>());
        UserPlace place = new UserPlace(nextId++, category, tag, name, address);
        places.add(place);
    }

    ;

    @Override
    public boolean update(long chatId, long id, String name, String address) {
        ArrayList<UserPlace> places = table.get(chatId);

        if (places == null) {
            return false;
        }

        for (UserPlace place : places) {
            if (place.getId() == id) {
                place.setName(name);
                place.setAddress(address);
                return true;
            }
        }

        return false;
    }

    ;

    @Override
    public boolean delete(long chatId, long id) {
        ArrayList<UserPlace> places = table.get(chatId);

        if (places == null) {
            return false;
        }

        return places.removeIf(p -> p.getId() == id);
    }
}
