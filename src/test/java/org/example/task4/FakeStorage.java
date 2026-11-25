package org.example.task4;

import org.example.storage.places.UserPlace;
import org.example.storage.places.UserPlaceRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Хранит пользовательские места в памяти в течение одного теста.
 */
public class FakeStorage implements UserPlaceRepository {

    /**
     *  Идентификатор, который получит следующее добавленное место.
     */
    private long nextId = 1;

    /**
     * Связывает место с владельцем и городом.
     *
     * @param chatId идентификатор чата владельца
     * @param city   город, в который добавили место
     * @param place  сохранённое место
     */
    private record Entry(long chatId, String city, UserPlace place) {
    }

    /**
     * Места всех чатов в порядке добавления.
     */
    private final List<Entry> entries = new ArrayList<>();


    @Override
    public List<UserPlace> getPlaces(long chatId, String city, String tag) {
        List<UserPlace> result = new ArrayList<>();
        for (Entry entry : entries) {
            UserPlace place = entry.place();
            if (entry.chatId() == chatId && entry.city().equals(city) && place.getTag().equals(tag)) {
                result.add(new UserPlace(place.getId(), place.getCategory(),
                        place.getTag(), place.getName(), place.getAddress()));
            }
        }
        return result;
    }

    @Override
    public void add(long chatId, String city, String category, String tag,
                    String name, String address) {
        UserPlace place = new UserPlace(nextId++, category, tag, name, address);
        entries.add(new Entry(chatId, city, place));
    }


    @Override
    public boolean update(long chatId, long id, String name, String address) {
        for (Entry entry : entries) {
            UserPlace place = entry.place();
            if (entry.chatId() == chatId && place.getId() == id) {
                place.setName(name);
                place.setAddress(address);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean delete(long chatId, long id) {
        return entries.removeIf(entry -> entry.chatId() == chatId
                && entry.place().getId() == id);
    }
}
