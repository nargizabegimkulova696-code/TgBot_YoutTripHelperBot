package org.example.catalog.city;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Интерфейс, созданный для работы с каталогом и его данными
 */
public interface CityCatalog {

    /**
     * Класс хранит данные об одном городе
     */
    class Info {
        /**
         * Название города
         */
        private final String name;

        /**
         * Статус города
         */
        private final String status;

        /**
         * Население города
         */
        private final String population;

        /**
         * Площадь города
         */
        private final String area;

        /**
         * Описание города
         */
        private final String description;

        /**
         * Климат в городе
         */
        private final String climate;

        /**
         * Интересные факты о городе
         */
        private final List<String> facts;

        /**
         * Конструктор инициализирует поля класса данными о городе
         */
        Info(String name, String status, String population, String area, String description, String climate, List<String> facts) {
            this.name = name;
            this.status = status;
            this.population = population;
            this.area = area;
            this.description = description;
            this.climate = climate;
            this.facts = facts;
        }

        /**
         * Возврат названия города
         */
        public String getName() {
            return name;
        }

        /**
         * Метод форматирует данные о городе в текст
         */
        public String formattedInfo() {
            List<String> blocks = new ArrayList<>();

            if (StringUtils.isNotBlank(status)) {
                blocks.add(name + " - " + status);
            } else {
                blocks.add(name);
            }

            if (StringUtils.isNotBlank(population)) {
                blocks.add("Население: " + population);
            }

            if (StringUtils.isNotBlank(area)) {
                blocks.add("Площадь: " + area);
            }

            if (StringUtils.isNotBlank(description)) {
                blocks.add(description);
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

    /**
     * Поиск города в каталоге по его названию
     */
    String findCity(String nameCity);


    /**
     * Поиск карточки с описанием города по его названию
     * @param nameCity - название города
     * @return карточка с описанием города или null, если города нет в каталоге
     */
    Info findInfo(String nameCity);
}
