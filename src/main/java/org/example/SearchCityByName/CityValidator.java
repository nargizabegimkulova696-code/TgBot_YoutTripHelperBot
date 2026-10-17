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
     * Нормализация строки
     */
    private final ToNormalStr toNormal = new ToNormalStr();

    /**
     * Инициализация каталога
     */
    public CityValidator(CityCatalog catalog) {
        this.catalog = catalog;
    }

    /**
     * Проверка на хранения города введенного пользователе в списке допустимых городов
     */
    public boolean contain(String cityName) {
        if (cityName == null) {
            return false;
        }

        String normalCity = toNormal.normal(cityName);

        return catalog.findCity(normalCity) != null;
    }
}
