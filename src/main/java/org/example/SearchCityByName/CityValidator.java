package org.example.SearchCityByName;

import org.example.Catalog.CityCatalog;

/**
 * Сверка данных введенных пользователем со словарем
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
     * Проверка на хранения города введенного пользователе в списке допустимых городов
     */
    public String contain(String cityName) {
        String city = catalog.findCity(cityName);
        return city == null ? "" : city;
    }
}
