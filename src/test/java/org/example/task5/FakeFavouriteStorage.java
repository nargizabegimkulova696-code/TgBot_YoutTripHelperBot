package org.example.task5;

import org.example.command.places.Place;
import org.example.storage.favourites.FavouritePlace;
import org.example.storage.favourites.UserFavouriteRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Хранит избранные места в памяти в течение одного теста
 */
public class FakeFavouriteStorage implements UserFavouriteRepository {

    /**
     * Идентификатор следующей записи в избранном
     */
    private long nextId = 1;

    /**
     * Связывает избранное место с владельцем
     */
    private record Entry(long chatId, FavouritePlace place) {
    }

    /**
     * Избранные места всех чатов в порядке добавления
     */
    private final List<Entry> entries = new ArrayList<>();

    @Override
    public boolean addFavourite(long chatId, String city, String category, Place place) {
        String name = place.getName();
        String address = place.getAddress();

        for (Entry entry : entries) {
            FavouritePlace saved = entry.place();
            if (entry.chatId() == chatId
                    && saved.getCity().equals(city)
                    && saved.getCategory().equals(category)
                    && saved.getName().equals(name)
                    && saved.getAddress().equals(address)) {
                return false;
            }
        }

        FavouritePlace saved = new FavouritePlace(nextId++, city, category, name, address);
        entries.add(new Entry(chatId, saved));
        return true;
    }

    @Override
    public List<FavouritePlace> getFavourites(long chatId, String category) {
        List<FavouritePlace> result = new ArrayList<>();
        for (Entry entry : entries) {
            FavouritePlace place = entry.place();
            if (entry.chatId() == chatId && place.getCategory().equals(category)) {
                result.add(place);
            }
        }
        return result;
    }

    @Override
    public boolean deleteFavourite(long chatId, long favouriteId) {
        return entries.removeIf(entry -> entry.chatId() == chatId
                && entry.place().getId() == favouriteId);
    }
}