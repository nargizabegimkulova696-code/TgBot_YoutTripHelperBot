package org.example.command.city;

import java.io.IOException;

/**
 * Интерфейс для работы с категорией "о городе"
 */
public interface CityProvider {
    /**
     * Метод возвращает информацию о городе
     *
     * @param city - название города
     * @return City - карточка города
     */
    City getInfo(String city) throws IOException;
}
