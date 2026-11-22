package org.example.server;

import org.apache.commons.lang3.StringUtils;
import org.example.command.city.City;
import org.example.command.city.CityProvider;

import java.io.IOException;

/**
 * Предоставляет доступ к данным о городе
 */
public class CityServer {
    /**
     * Данные о городах, которые получаем от api
     */
    private final CityProvider provider;

    /**
     * Данные о городах из csv каталога, для городов с населением >500тыс.
     */
    private final CityProvider catalog;


    /**
     * Инициализация провайдера и каталога
     */
    public CityServer(CityProvider provider, CityProvider catalog) {

        this.provider = provider;
        this.catalog = catalog;
    }

    /**
     * Метод, который ищет город по его названию
     */
    public String findCityByName(String city) {
        try {
            City cityInfo = provider.getInfo(city);
            return cityInfo.getTitle();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Метод, который ищет информацию о городе по его названию, при выборе категории about_city
     */
    public String findInfoForCity(String city) {
        try {
            City info;

            if (catalog.getInfo(city) == null) {
                info = provider.getInfo(city);
                return info.formattedInfoWithMinPop();
            }

            info = catalog.getInfo(city);

            return info.formattedInfoWithBigPop();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Возвращает ссылку, если для города есть фото.
     * Возвращает null, если ссылки на фото нет или при запросе к апи произошла ошибка
     *
     * @param city - город, для которого делается проверка
     */
    public String getPhotoUriForCity(String city) {
        try {
            String photoUri = provider.getInfo(city).getPhotoUrl();

            if (StringUtils.isBlank(photoUri)) {
                return null;
            }

            return photoUri;
        } catch (IOException e) {
            return null;
        }
    }
}
