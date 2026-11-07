package org.example.cityLoader;

import org.example.command.city.CityProvider;
import org.example.command.city.CsvCatalogCity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Метод этого класса инициализирует каталог городов
 */
public class CsvCityLoader implements CityLoader {
    @Override
    public CityProvider load() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream("/city_info.csv")) {
            if (stream == null) {
                throw new IOException("Файл /city_info.csv не найден");
            }

            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

            return new CsvCatalogCity(reader);
        }
    }
}
