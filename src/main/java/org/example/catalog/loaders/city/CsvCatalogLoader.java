package org.example.catalog.loaders.city;

import org.example.catalog.city.CityCatalog;
import org.example.catalog.city.CsvCityCatalog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Метод этого класса инициализирует каталог городов
 */
public class CsvCatalogLoader implements CityCatalogLoader {
    @Override
    public CityCatalog load() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream("/city_info.csv")) {
            if (stream == null) {
                throw new IOException("Файл /city_info.csv не найден");
            }

            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

            return new CsvCityCatalog(reader);
        }
    }
}
