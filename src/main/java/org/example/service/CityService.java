package org.example.service;

import org.example.catalog.city.CityCatalog;
import org.example.catalog.city.CityCatalog.Info;

/**
 * Сервис для поиска городов и их справочных карточек (статус, население, площадь,
 * описание, климат, факты) в каталоге городов
 */
public class CityService {
    /**
     * Каталог городов, из которого сервис берёт названия городов и справочные данные
     */
    private final CityCatalog cityCatalog;

    /**
     * Ответ пользователю, если карточки города в каталоге нет
     */
    private static final String NOT_FOUND_CITY = "Информации о городе не найдено";

    /**
     * В конструкторе инициализируем поле каталога
     */
    public CityService(CityCatalog catalog) {
        this.cityCatalog = catalog;
    }

    /**
     * Метод, который ищет город по его названию в каталоге
     */
    public String findCityByName(String city) {
        return cityCatalog.findCity(city);
    }

    /**
     * Формирует текст справочной информации города для кнопки about_city
     *
     * @param city название города из каталога
     * @return текст информации или сообщение "Информации о городе не найдено", если информации нет
     */
    public String findInfoForCity(String city) {
        if (cityCatalog.findInfo(city) == null) {
            return NOT_FOUND_CITY;
        }

        Info info = cityCatalog.findInfo(city);

        return info.formattedInfo();
    }
}
