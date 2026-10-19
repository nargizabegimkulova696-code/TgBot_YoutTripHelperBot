package org.example.Catalog;

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
     * В конструкторе реализуется чтение данных из файла и инициализация словаря
     */
    public CsvCityCatalog(InputStreamReader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        String line;

        while ((line = reader.readLine()) != null) {
            String key = normal(line);

            if (!key.isEmpty()) {
                cities.put(key, line);
            }
        }
    }

    private String normal(String str) {

        if (str == null || str.isBlank()) {
            return "";
        }

        String result = str
                .toLowerCase()
                .replace('ё', 'е')
                .replace('-', ' ')
                .replaceAll("\\s+", " ")
                .trim();

        return result;
    }

    @Override
    public String findCity(String nameCity) {
        return cities.get(normal(nameCity));
    }
}
