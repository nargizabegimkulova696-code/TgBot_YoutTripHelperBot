package org.example.command.city;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс содержит информацию о городе, распределенную по блокам.
 * Для городов от 500 тыс. населения доступны блоки с климатом и интересными фактами.
 */
public class City {
    /**
     * Название города
     */
    private final String title;

    /**
     * Статус города
     */
    private final String description;

    /**
     * Дата основания города
     */
    private final String inception;

    /**
     * Население
     */
    private final String population;

    /**
     * Площадь
     */
    private final String area;

    /**
     * Краткое описание города
     */
    private final String extract;

    /**
     * Описание климата в городе
     */
    private final String climate;

    /**
     * Интересные факты о городе
     */
    private final List<String> facts;

    /**
     * Конструктор для городов с населением < 500 тыс.
     */
    public City(String title, String description, String inception, String population, String area, String extract) {

        this.title = title;
        this.description = description;
        this.inception = inception;
        this.population = population;
        this.area = area;
        this.extract = extract;

        this.climate = "";
        this.facts = List.of();
    }

    /**
     * Конструктор для городов с населением > 500 тыс.
     */
    City(String title, String description, String population,
         String area, String extract, String climate, List<String> facts) {

        this.title = title;
        this.description = description;
        this.population = population;
        this.area = area;
        this.extract = extract;
        this.climate = climate;
        this.facts = facts;

        this.inception = "";
    }

    /**
     * Возврат названия города
     */
    public String getTitle() {
        return title;
    }

    /**
     * Форматирование данных о городе с населением <500 тыс.
     */
    public String formattedInfoWithMinPop() {
        List<String> blocks = new ArrayList<>();

        if (StringUtils.isBlank(extract)) {

            if (StringUtils.isNotBlank(description)) {
                blocks.add(title + " - " + description);
            } else {
                blocks.add(title);
            }

        } else {
            blocks.add(extract);
        }

        if (StringUtils.isNotBlank(inception)) {
            blocks.add("Дата основания - " + inception);
        }

        if (StringUtils.isNotBlank(population)) {
            blocks.add("Население - " + population + " человек");
        }

        if (StringUtils.isNotBlank(area)) {
            blocks.add("Площадь - " + area + " км²");
        }

        return String.join("\n\n", blocks);
    }

    /**
     * Метод форматирует данные о городе с населением > 500 тыс.
     */
    public String formattedInfoWithBigPop() {
        List<String> blocks = new ArrayList<>();

        if (StringUtils.isNotBlank(description)) {
            blocks.add(title + " - " + description);
        } else {
            blocks.add(title);
        }

        if (StringUtils.isNotBlank(population)) {
            blocks.add("Население: " + population);
        }

        if (StringUtils.isNotBlank(area)) {
            blocks.add("Площадь: " + area);
        }

        if (StringUtils.isNotBlank(extract)) {
            blocks.add(extract);
        }

        if (StringUtils.isNotBlank(climate)) {
            blocks.add("Климат: " + climate);
        }

        if (!facts.isEmpty()) {
            blocks.add("Интересные факты о городе: \n- " + String.join("\n- ", facts));
        }

        return String.join("\n\n", blocks);
    }
}
