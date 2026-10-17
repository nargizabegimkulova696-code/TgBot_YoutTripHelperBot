package org.example.Catalog;

import org.example.SearchCityByName.ToNormalStr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Map;

/**
 * Считывает данные из csv файла и создает на основе этих данных словарь
 */
public class CsvCityCatalog implements CityCatalog {
    /**
     * Словарь, в котором ключ - название города, значение - название города
     */
    private final Map<String, String> cities = new HashMap<>();

    /**
     * Нормализация строки
     */
    private final ToNormalStr toNormal = new ToNormalStr();

    /**
     * В конструкторе реализуется чтение данных из файла и инициализация словаря
     */
    public CsvCityCatalog(InputStreamReader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String name = toNormal.normal(line);

            cities.put(name, line);
        }
    }

    @Override
    public String findCity(String normalized) {
        return cities.get(normalized);
    }
}
