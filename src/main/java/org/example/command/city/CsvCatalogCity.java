package org.example.command.city;

import org.example.StringUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Чтение данных из csv файла и создание словаря на основе этих данных
 */
public class CsvCatalogCity implements CityProvider {
    /**
     * Словарь, в котором ключ - название города, значение - карточка с описанием города
     */
    private final Map<String, City> cities = new ConcurrentHashMap<>();

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

            String title = parts[0].trim();
            String description = parts[1].trim();
            String population = parts[2].trim();
            String area = parts[3].trim();
            String extract = parts[4].trim();
            String climate = parts[5].trim();
            List<String> facts = parseFacts(parts[6].trim());

            City city = new City(title, description, population, area, extract, climate, facts);

            String key = StringUtilities.normalizeCityName(city.getTitle());

            if (!key.isEmpty()) {
                cities.put(key, city);
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
    public City getInfo(String title) {
        return cities.get(StringUtilities.normalizeCityName(title));
    }
}

