package org.example.storage.favourites;

import org.example.command.places.Place;

/**
 * Место, сохранённое пользователем в избранном
 */
public class FavouritePlace extends Place {
    /**
     * Идентификатор записи в избранном
     */
    private final long id;

    /**
     * Город, в котором находится место
     */
    private final String city;

    /**
     * Категория места
     */
    private final String category;

    /**
     * Создание места из записи в избранном
     */
    public FavouritePlace(long id, String city, String category, String name, String address) {
        super(name, address);
        this.id = id;
        this.city = city;
        this.category = category;
    }

    /**
     * Идентификатор записи в избранном
     */
    public long getId() {
        return id;
    }

    /**
     * Город, в котором находится место
     */
    public String getCity() {
        return city;
    }

    /**
     * Категория места
     */
    public String getCategory() {
        return category;
    }
}