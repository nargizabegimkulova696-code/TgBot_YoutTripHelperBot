package org.example.catalog.loaders.city;

import org.example.catalog.city.CityCatalog;

import java.io.IOException;

/**
 * Интерфейс для загрузчика каталога городов
 */
public interface CityCatalogLoader {
    /**
     * Загрузить каталог городов.
     *
     * @return загруженный каталог городов
     * @throws IOException если файл не был найден или данные,
     *                     прочитанные из файла, не соответствуют ожидаемым
     */
    CityCatalog load() throws IOException;
}
