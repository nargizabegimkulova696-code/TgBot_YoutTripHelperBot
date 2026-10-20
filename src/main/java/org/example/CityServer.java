package org.example;

import org.example.catalog.CityCatalog;
import org.example.catalog.CityCatalog.Info;
import org.example.command.AboutCity;

/**
 * Предоставляет доступ к каталогу с данными
 */
public class CityServer {
    /**
     * Используемый в программе каталог с данными
     */
    private final CityCatalog catalog;

    /**
     * Константное сообщение, которое получает пользователь, если данных о городе нет в каталоге
     */
    private static final String NOT_FOUND_CITY = "Информации о городе не найдено";

    /**
     * В конструкторе инициализируем поле каталога
     */
    public CityServer(CityCatalog catalog) {
        this.catalog = catalog;
    }

    /**
     * Метод, который ищет город по его названию в каталоге
     */
    public String findCityByName(String city) {
        return catalog.findCity(city);
    }

    /**
     * Метод, который ищет информацию о городе по его названию, при выборе категории about_city
     */
    public String findInfoForCity(String city) {
        if (catalog.findInfo(city) == null) {
            return NOT_FOUND_CITY;
        }

        Info info = catalog.findInfo(city);
        AboutCity aboutCity = new AboutCity();

        return aboutCity.format(info);
    }
}
