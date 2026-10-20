package org.example.SearchCityByName;

import org.example.Catalog.CityCatalog;

/**
 * Класс реализует поиск города, введенного пользователем, по его названию в каталоге
 */
public class CityValidator {
    /**
     * Хранит список допустимых городов
     */
    private final CityCatalog catalog;

    /**
     * Инициализация каталога
     */
    public CityValidator(CityCatalog catalog) {
        this.catalog = catalog;
    }

    /**
     * Возврат каталога
     */
    public CityCatalog getCatalog() {
        return catalog;
    }

    /**
     * Поиск города по его названию в списке городов
     */
    public String contain(String cityName) {
        String city = catalog.findCity(cityName);
        return city == null ? "" : city;
    }
}
