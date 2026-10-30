package org.example.catalog.loaders.tags;

import org.example.catalog.tags.CsvTagsCatalog;
import org.example.catalog.tags.TagsCatalog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Класс создает каталоги для категорий food, culture, attractions или выбрасывает исключение,
 * если файл для каталога не был найден или при чтении данных из файла произошла ошибка
 */
public class CsvTagsCatalogLoader implements TagsCatalogLoader {
    @Override
    public TagsCatalog load(String filename) throws IOException {

        try (InputStream input = getClass().getResourceAsStream(filename)) {
            if (input == null) {
                throw new IOException("Не удалось найти файл " + filename);
            }
            InputStreamReader stream = new InputStreamReader(input, StandardCharsets.UTF_8);

            return new CsvTagsCatalog(stream);
        }
    }

}
