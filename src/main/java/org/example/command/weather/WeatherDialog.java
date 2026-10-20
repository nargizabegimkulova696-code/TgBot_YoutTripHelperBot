package org.example.command.weather;

import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;

import org.example.session.BotSession;
import org.example.session.DialogState;

import java.io.IOException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Диалог с inline-кнопкой weather
 */
public class WeatherDialog {
    /**
     * Максимальная длина периода прогноза, включая сегодня
     */
    private final int MAX_DAYS = 14;

    /**
     * Период в формате регулярного выражения "дд.мм - дд.мм".
     * Между датами допускаются дефис и пробелы вокруг них.
     */
    private final Pattern PERIOD = Pattern.compile("(\\d{1,2})\\.(\\d{1,2})\\s*[-]\\s*(\\d{1,2})\\.(\\d{1,2})");

    /**
     * Формат вывода даты в ответе бота
     */
    private final DateTimeFormatter DAY_MONTH = DateTimeFormatter.ofPattern("dd.MM");

    /**
     * Источник прогноза погоды, чтобы класс функционировал без подключения API
     */
    private final WeatherProvider provider;

    public WeatherDialog(WeatherProvider provider) {
        this.provider = provider;
    }

    /**
     * Начало диалога: проверяет, что город уже выбран, и просит ввести период.
     * Если города нет, остаёмся в текущем состоянии и просим выбрать город.
     * @param session сессия пользователя (город и состояние диалога)
     * @return ответ от бота (текстовое сообщение + inline-клавиатура)
     */
    public DialogResponse start(BotSession session) {
        DialogResponse response;
        KeyboardFactory keyboard = new KeyboardFactory();

        if (session.getCity() == null) {
            response = new DialogResponse("Сначала выберите город, для этого напишите его название)");
            return response;
        }

        session.setState(DialogState.CAT_WEATHER);
        response = new DialogResponse("На какие даты вас интересует погода? Напишите период в формате дд.мм - дд.мм. "
                + "Длина периода не больше " + MAX_DAYS + " дней.", keyboard.build(session.getState()));
        return response;
    }

    /**
     * Обработка случаев связанных с введенным периодом дат
     * Если ввод некорректный - просит пользователя ввести заново, иначе возвращаем результат
     * @param text сообщение пользователя в формате дд.мм - дд.мм
     * @param session сессия пользователя
     * @return сообщение: (прогноз погоды или сообщение о том, что пошло не так) + inline-клавиатура
     */
    public DialogResponse handleDates(String text, BotSession session) {
        KeyboardFactory keyboard = new KeyboardFactory();

        Matcher matcher = PERIOD.matcher(text.trim());
        if (!matcher.matches()) {
            return new DialogResponse("Некорректный ввод.\n " +
                    "Напишите период в формате дд.мм - дд.мм, например 12.10 - 18.10.");
        }

        // Обработка различных случаев, учитывая не введенный год
        LocalDate today = LocalDate.now();
        LocalDate from;
        LocalDate to;
        try {
            from = LocalDate.of(today.getYear(), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(1)));
            if (from.isBefore(today)) {
                from = from.plusYears(1);
            }
            to = LocalDate.of(from.getYear(), Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(3)));
            if (to.isBefore(from)) {
                to = to.plusYears(1);
            }
        } catch (DateTimeException e) {
            return new DialogResponse("Такой даты не существует. Напишите период в формате дд.мм - дд.мм.");
        }

        if (ChronoUnit.DAYS.between(from, to) + 1 > MAX_DAYS) {
            return new DialogResponse("Период слишком длинный. Выберите не больше " + MAX_DAYS + " дней.");
        }

        if (to.isAfter(today.plusDays(MAX_DAYS - 1))) {
            return new DialogResponse("Погоду можно посмотреть только на ближайшие "
                    + MAX_DAYS + " дней. Выберите другие даты.");
        }

        String city = session.getCity();
        try {
            return new DialogResponse(format(city, from, to, provider.getForecast(city, from, to)),
                    keyboard.build(session.getState()));
        } catch (IOException e) {
            return new DialogResponse("Не удалось получить погоду. Попробуйте позже.",
                    keyboard.build(session.getState()));
        }
    }

    /**
     * Создание ответного сообщения в виде: заголовок + в каждой строке прогноз погоды на один день
     * Пример строки: "22.10: -1...+7°C, дождь".
     */
    private String format(String city, LocalDate from, LocalDate to, List<WeatherProvider.Day> days) {
        String text = "Погода в городе " + city
                + " с " + from.format(DAY_MONTH) + " по " + to.format(DAY_MONTH) + ":";

        for (WeatherProvider.Day day : days) {
            text += "\n" + day.date().format(DAY_MONTH) + ": "
                    + signed(day.minTemp()) + "..." + signed(day.maxTemp()) + "°C, "
                    + day.description();
        }
        return text;
    }

    /**
     * Добавление знака, для положительной температуры
     */
    private String signed(double temperature) {
        long rounded = Math.round(temperature);
        if (rounded > 0) {
            return "+" + rounded;
        }
        return String.valueOf(rounded);
    }
}