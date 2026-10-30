package org.example.catalog.loaders.tags;

import org.example.catalog.tags.TagsCatalog;

import java.io.IOException;

/**
 * Интерфейс для инициализации каталогов: food, culture, attractions
 */
public interface TagsCatalogLoader {
    /**
     * Метод инициализирует каталоги: food, culture, attractions
     *
     * @param filename - имя файла, из которого читаются данные о каталоге
     * @return TagsCatalog, возврат каталога
     */
    TagsCatalog load(String filename) throws IOException;
}
