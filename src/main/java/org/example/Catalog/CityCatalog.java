package org.example.Catalog;

import java.util.List;

/**
 * Интерфейс, созданный для работы с каталогами данных
 */
public interface CityCatalog {

    /**
     * Класс, который хранит данные об одном городе
     * @param name - название города
     * @param status - статус в России (столица региона или в какой области находится город)
     * @param population - население
     * @param area - площадь
     * @param description - краткое описание
     * @param climate - климат
     * @param facts - интересные факты
     */
    record Info(String name, String status, String population, String area,
                String description, String climate, List<String> facts) {
    }

    /**
     * Поиск города в каталоге по его названию
     */
    public String findCity(String nameCity);


    /**
     * Поиск карточки с описанием города по его названию
     * @param nameCity - название города
     * @return карточка с описанием города или null, если города нет в каталоге
     */
    Info findInfo(String nameCity);
}
