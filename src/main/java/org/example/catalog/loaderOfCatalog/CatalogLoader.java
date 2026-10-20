package org.example.catalog.loaderOfCatalog;

import org.example.catalog.CityCatalog;

import java.io.IOException;

/**
 * Интерфейс для инициализации каталога
 */
public interface CatalogLoader {
    /**
     * Инициализация каталога и его возврат
     */
    public CityCatalog load() throws IOException;
}
