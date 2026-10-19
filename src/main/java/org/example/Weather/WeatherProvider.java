package org.example.Weather;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface WeatherProvider {
    /**
     * Прогноз по дням для города на период включительно
     *
     * @param city название города
     * @param from первый день периода
     * @param to   последний день периода
     * @return список дней
     * @throws IOException если прогноз получить не удалось
     */
    List<Day> getForecast(String city, LocalDate from, LocalDate to) throws IOException;

    /**
     * Прогноз погоды на один день.
     *
     * @param date        дата
     * @param minTemp     минимальная температура за день
     * @param maxTemp     максимальная температура за день
     * @param description краткое описание погоды
     */
    record Day(LocalDate date, double minTemp, double maxTemp, String description) {
    }
}
