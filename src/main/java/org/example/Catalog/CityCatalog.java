package org.example.Catalog;

import java.util.List;

/**
 * Интерфейс, создан для работы с каталогами данных
 */
public interface CityCatalog {

    /**
     * Данные об одном городе
     *
     * @param name        название города
     * @param status      статус в России (столица региона например)
     * @param population  население
     * @param area        площадь
     * @param description краткое описание
     * @param climate     климат
     * @param facts       интересные факты
     */
    record Info(String name, String status, String population, String area,
                String description, String climate, List<String> facts) {
    }

    /**
     * Возврат города по его названию
     */
    public String findCity(String nameCity);


    /**
     * Возврат справки о городе по его названию
     *
     * @param nameCity название города в любом регистре
     * @return справка о городе или null, если города нет
     */
    Info findInfo(String nameCity);
}
