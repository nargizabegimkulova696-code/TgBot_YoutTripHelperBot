package org.example.Catalog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CatalogLoader {

    public CsvCityCatalog load() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/cities.csv");

        if (stream == null) {
            throw new IOException("Файл cities.csv не найден в resources");
        }

        try (InputStreamReader input = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return new CsvCityCatalog(input);
        }
    }
}
