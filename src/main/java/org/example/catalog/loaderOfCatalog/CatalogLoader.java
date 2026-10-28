package org.example.catalog.loaderOfCatalog;

import org.example.catalog.CityCatalog;

import java.io.IOException;

/**
 * Интерфейс для создания каталогов
 */
public interface CatalogLoader {
    /**
     * Метод создает каталог либо выбрасывает исключение, если файл не был найден или данные,
     * прочитанные из файла, не соответствуют ожидаемым
     */
    CityCatalog load() throws IOException;
}
