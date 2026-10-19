package org.example.Catalog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Считывает данные из csv файла и создает на основе этих данных словарь
 */
public class CsvCityCatalog implements CityCatalog {
    /**
     * Словарь, в котором ключ - название города, значение - справка о городе
     */
    private final Map<String, Info> cities = new HashMap<>();

    /**
     * Количество данных: name, status, population, area, description, climate, facts
     */
    private final int columns = 7;

    /**
     * В конструкторе реализуется чтение данных из файла и инициализация словаря.
     * (Отсчет со второй строки, так как первая - заголовок)
     */
    public CsvCityCatalog(InputStreamReader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        reader.readLine(); // первая строка заголовок - ее пропускают

        String line;
        int number = 1; // номер строки в файле.

        while ((line = reader.readLine()) != null) {
            number++;

            if (line.isBlank()) {
                continue;
            }

            String[] parts = line.split(";", -1);

            if (parts.length != columns) {
                throw new IOException("Строка " + number + ": ожидается колонок " + columns
                        + ", найдено " + parts.length);
            }

            Info info = new Info(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(),
                    parts[4].trim(), parts[5].trim(), parseFacts(parts[6]));

            String key = normal(info.name());

            if (!key.isEmpty()) {
                cities.put(key, info);
            }
        }
    }

    /**
     * Разбивает колонку с фактами на список, факты разделены символом |
     */
    private List<String> parseFacts(String text) {
        List<String> facts = new ArrayList<>();

        for (String fact : text.split("\\|")) {
            if (!fact.isBlank()) {
                facts.add(fact.trim());
            }
        }

        return facts;
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
        Info info = cities.get(normal(nameCity));
        if (info == null) {
            return null;
        }
        return info.name();
    }

    @Override
    public Info findInfo(String nameCity) {
        return cities.get(normal(nameCity));
    }
}

