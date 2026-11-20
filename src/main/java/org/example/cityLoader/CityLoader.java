package org.example.cityLoader;

import org.example.command.city.CityProvider;

import java.io.IOException;

/**
 * Интерфейс для загрузчика каталога городов
 */
public interface CityLoader {
    /**
     * Загрузить каталог городов.
     *
     * @return загруженный каталог городов
     * @throws IOException если файл не был найден или данные,
     *                     прочитанные из файла, не соответствуют ожидаемым
     */
    CityProvider load() throws IOException;
}
