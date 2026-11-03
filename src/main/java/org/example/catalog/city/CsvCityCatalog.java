package org.example.catalog.city;

import org.example.StringUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Чтение данных из csv файла и создание словаря на основе этих данных
 */
public class CsvCityCatalog implements CityCatalog {
    /**
     * Словарь, в котором ключ - название города, значение - карточка с описанием города
     */
    private final Map<String, Info> cities = new HashMap<>();

    /**
     * Максимальное кол-во данных на город: name, status, population, area, description, climate, facts
     */
    private static final int COLUMNS = 7;

    /**
     * В конструкторе реализуется чтение данных из файла и инициализация словаря.
     * Чтение данных из файла начинается со второй строки, так как первая - заголовок
     */
    public CsvCityCatalog(InputStreamReader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        //пропуск заголовка файла
        reader.readLine();

        String line;
        int number = 1;

        while ((line = reader.readLine()) != null) {
            number++;

            if (line.isBlank()) {
                continue;
            }

            String[] parts = line.split(";", -1);

            if (parts.length != COLUMNS) {
                throw new IOException("Строка " + number + ": ожидается колонок " + COLUMNS
                        + ", найдено " + parts.length);
            }

            String name = parts[0].trim();
            String status = parts[1].trim();
            String population = parts[2].trim();
            String area = parts[3].trim();
            String description = parts[4].trim();
            String climate = parts[5].trim();
            List<String> facts = parseFacts(parts[6].trim());

            Info info = new Info(name, status, population, area, description, climate, facts);

            String key = StringUtilities.normal(info.getName());

            if (!key.isEmpty()) {
                cities.put(key, info);
            }
        }
    }

    /**
     * Разбитие строки с фактами о городе в список
     * Разделитель для фактов о городе - |
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

    @Override
    public String findCity(String nameCity) {
        Info info = cities.get(StringUtilities.normal(nameCity));
        if (info == null) {
            return null;
        }
        return info.getName();
    }

    @Override
    public Info findInfo(String nameCity) {
        return cities.get(StringUtilities.normal(nameCity));
    }
}

