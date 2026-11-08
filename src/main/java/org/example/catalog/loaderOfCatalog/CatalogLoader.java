package org.example.catalog.loaderOfCatalog;

import org.example.catalog.CityCatalog;

import java.io.IOException;

/**
 * Интерфейс для загрузчика каталога городов
 */
public interface CatalogLoader {
    /**
     * Загрузить каталог городов.
     *
     * @return загруженный каталог городов
     * @throws IOException если файл не был найден или данные,
     *                     прочитанные из файла, не соответствуют ожидаемым
     */
    CityCatalog load() throws IOException;
}
