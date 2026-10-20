package org.example.command.weather;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface WeatherProvider {

    /**
     * Прогноз по дням для города на период (с from по to включительно)
     * @param city название города
     * @param from первый день периода
     * @param to последний день периода
     * @return список дней
     * @throws IOException если прогноз получить не удалось
     */
    List<Day> getForecast(String city, LocalDate from, LocalDate to) throws IOException;

    /**
     * Прогноз погоды на день
     * @param date дата
     * @param minTemp минимальная температура в этот день
     * @param maxTemp максимальная температура в этот день
     * @param description краткое описание погоды
     */
    record Day(LocalDate date, double minTemp, double maxTemp, String description) {
    }
}
