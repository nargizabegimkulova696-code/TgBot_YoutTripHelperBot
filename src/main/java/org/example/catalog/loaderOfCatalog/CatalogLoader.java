package org.example.catalog.loaderOfCatalog;

import org.example.catalog.CityCatalog;

import java.io.IOException;

/**
 * Интерфейс для создания каталогов
 */
public interface CatalogLoader {
    /**
     * Метод, который создает каталог либо выбрасывает исключение, если чтение файда не прошло успешно
     */
    public CityCatalog load() throws IOException;
}
