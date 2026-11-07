package org.example.cityLoader;

import org.example.command.city.CityProvider;

import java.io.IOException;

/**
 * Интерфейс для создания каталогов
 */
public interface CityLoader {
    /**
     * Метод создает каталог либо выбрасывает исключение, если файл не был найден или данные,
     * прочитанные из файла, не соответствуют ожидаемым
     */
    CityProvider load() throws IOException;
}
