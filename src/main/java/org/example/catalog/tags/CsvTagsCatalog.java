package org.example.catalog.tags;

import org.apache.commons.lang3.StringUtils;
import org.example.StringUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс инициализирует каталог для одной из категорий: food, culture, attractions
 */
public class CsvTagsCatalog implements TagsCatalog {
    /**
     * Каталог, ключ которого это название города,
     * значение - строка, содержащая теги и места выбранной категории
     */
    private final Map<String, String> catalog = new HashMap<>();

    /**
     * Логирование ошибок при чтении из файла
     */
    private final Logger log = LoggerFactory.getLogger("CsvTagsCatalog");

    /**
     * Значение кол-ва столбцов, которое должно получиться при разбиении строки
     */
    private final static int COLUMNS = 2;

    /**
     * Кол-во тегов фиксировано для каждой категории
     */
    private final static int TAGS_COLUMNS = 5;

    /**
     * Чтение данных из файла и инициализации каталога
     */
    public CsvTagsCatalog(InputStreamReader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        String line;
        int num = 0;

        while ((line = reader.readLine()) != null) {
            num++;
            if (line.isBlank()) {
                continue;
            }

            String[] parts = line.split(";", -1);

            if (parts.length != COLUMNS) {
                throw new IOException("В строке " + num + " ожидалось " + COLUMNS + " столбца, а их " + parts.length);
            }

            String city = StringUtilities.normal(parts[0].trim());
            String tags = parts[1].trim();

            catalog.put(city, tags);
        }
    }

    @Override
    public String getPlaces(String city, String tag) {
        String tags = catalog.get(StringUtilities.normal(city));

        if (tags == null) {
            return null;
        }

        Map<String, String> tagsTable = createTable(tags);

        if (tagsTable.isEmpty()) {
            return null;
        }

        if (tagsTable.get(tag) == null) {
            return null;
        }

        return formatted(tagsTable.get(tag));
    }

    ;

    /**
     * Метод возвращает каталог тегов для города. Ключ - callback тега, значение - места по тегу
     */
    private Map<String, String> createTable(String tags) {
        Map<String, String> table = new HashMap<>();

        String[] partsTags = tags.split("!", -1);

        if (partsTags.length != TAGS_COLUMNS) {
            log.error("Ожидалось " + TAGS_COLUMNS + " столбцов в строке " + tags);
            return table;
        }

        for (String placesInTag : partsTags) {
            String[] partsTag = placesInTag.split("/", -1);

            if (partsTag.length != COLUMNS) {
                log.error("Ожидалось " + COLUMNS + " столбцов в " + placesInTag);
                continue;
            }

            String callback = partsTag[0].trim();
            String places = partsTag[1].trim();

            table.put(callback, places);
        }

        return table;
    }

    /**
     * Форматирует строку мест в текстовое сообщение пользователю
     */
    private String formatted(String notFormattedPlaces) {
        List<String> blocks = new ArrayList<>();

        String[] parts = notFormattedPlaces.split("\\?", -1);

        if (parts.length != COLUMNS) {
            log.error("Ожидалось " + COLUMNS + " столбцов в " + notFormattedPlaces);
            return null;
        }

        String text = parts[0].trim();
        String places = parts[1].trim();

        if (StringUtils.isBlank(places)) {
            return null;
        }

        blocks.add(text);

        for (String place : places.split("\\|", -1)) {
            if (StringUtils.isBlank(place)) {
                continue;
            }

            blocks.add("- " + place);
        }

        return String.join("\n\n", blocks);
    }
}
