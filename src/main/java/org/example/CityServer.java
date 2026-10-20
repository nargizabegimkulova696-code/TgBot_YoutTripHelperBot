package org.example;

import org.example.catalog.CityCatalog;
import org.example.catalog.CityCatalog.Info;
import org.example.command.AboutCity;

public class CityServer {
    private final CityCatalog catalog;

    private static final String NOT_FOUND_CITY = "Информации о городе не найдено";

    public CityServer(CityCatalog catalog) {
        this.catalog = catalog;
    }

    public String findCityByName(String city) {
        return catalog.findCity(city);
    }

    public String findInfoForCity(String city) {
        if (catalog.findInfo(city) == null) {
            return NOT_FOUND_CITY;
        }

        Info info = catalog.findInfo(city);
        AboutCity aboutCity = new AboutCity();

        return aboutCity.format(info);
    }
}
