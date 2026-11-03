package org.example.catalog.loaders.city;

import org.example.catalog.city.CityCatalog;

import java.io.IOException;

/**
 * Интерфейс для создания каталогов
 */
public interface CityCatalogLoader {
    /**
     * Метод создает каталог либо выбрасывает исключение, если файл не был найден или данные,
     * прочитанные из файла, не соответствуют ожидаемым
     */
    CityCatalog load() throws IOException;
}
