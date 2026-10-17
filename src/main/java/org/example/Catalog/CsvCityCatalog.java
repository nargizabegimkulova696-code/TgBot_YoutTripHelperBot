package org.example.Catalog;

import org.example.SearchCityByName.City;
import org.example.SearchCityByName.CityCatalog;
import org.example.SearchCityByName.ToNormalStr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Map;

public class CsvCityCatalog implements CityCatalog {
    private final Map<String, City> cities = new HashMap<>();
    private final ToNormalStr toNormal = new ToNormalStr();

    public CsvCityCatalog(InputStreamReader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] city = line.split(";");

            String name = toNormal.normal(city[0]);
            int population = Integer.parseInt(city[1]);

            cities.put(name, new City(name, population));
        }
    }

    @Override
    public City findCity(String normalized) {
        return cities.get(normalized);
    }

    ;
}
