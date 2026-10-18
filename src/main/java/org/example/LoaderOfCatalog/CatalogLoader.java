package org.example.LoaderOfCatalog;

import org.example.Catalog.CityCatalog;

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
