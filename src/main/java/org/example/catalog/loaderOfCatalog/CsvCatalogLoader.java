package org.example.catalog.loaderOfCatalog;

import org.example.catalog.CityCatalog;
import org.example.catalog.CsvCityCatalog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Реализация возврата csv каталога
 */
public class CsvCatalogLoader implements CatalogLoader {
    @Override
    public CityCatalog load() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/city_info.csv");

        if (stream == null) {
            throw new IOException("Файл /city_info.csv не найден");
        }

        try (InputStreamReader input = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return new CsvCityCatalog(input);
        }
    }
}
