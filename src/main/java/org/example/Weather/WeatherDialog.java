package org.example.Weather;

import org.example.DialogService.DialogResponse;
import org.example.DialogService.KeyboardFactory;
import org.example.Session.BotSession;
import org.example.Session.DialogState;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Диалог команды /weather
 */
public class WeatherDialog {
    /**
     * Максимальная длина периода включая сегодня
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
     * Источник прогноза, чтобы могло работать без API
     */
    private final WeatherProvider provider;

    public WeatherDialog(WeatherProvider provider) {
        this.provider = provider;
    }

    /**
     * Начало диалога: проверяет, что город уже выбран, и просит ввести период.
     * Если города нет, остаёмся в текущем состоянии и просим выбрать город.
     *
     * @param session сессия пользователя (город и состояние диалога)
     * @return ответ бота и новое состояние клавиатуры
     */
    public DialogResponse start(BotSession session) {
        DialogResponse response;
        KeyboardFactory keyboard = new KeyboardFactory();

        if (session.getCity() == null) {
            response = new DialogResponse("Сначала выберите город. Напишите его название.");
            return response;
        }

        session.setState(DialogState.CAT_WEATHER);
        response = new DialogResponse("На какие даты вас интересует погода? Напишите период в формате дд.мм - дд.мм. "
                + "Длина периода не больше " + MAX_DAYS + " дней.", keyboard.build(session.getState()));
        // Переходим в состояние ожидания дат: следующее сообщение придёт в handleDate
        return response;
    }

    /**
     * Ввод периода дат.
     * Если ввод некорректный - просит пользователя ввести заново, иначе возвращает результат
     *
     * @param text    сообщение пользователя с периодом
     * @param session сессия пользователя
     * @return прогноз или сообщение о том, что пошло не так
     */
    public DialogResponse handleDates(String text, BotSession session) {
        DialogResponse response;
        KeyboardFactory keyboard = new KeyboardFactory();

        Matcher matcher = PERIOD.matcher(text.trim());
        if (!matcher.matches()) {
            response = new DialogResponse("Некорректный ввод.\n " +
                    "Напишите период в формате дд.мм - дд.мм, например 12.10 - 18.10.");
            return response;
        }

        // Обработка разных случаев с неведенным годом
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
            response = new DialogResponse("Такой даты не существует. Напишите период в формате дд.мм - дд.мм.");
            return response;
        }

        if (ChronoUnit.DAYS.between(from, to) + 1 > MAX_DAYS) {
            response = new DialogResponse("Период слишком длинный. Выберите не больше " + MAX_DAYS + " дней.");
            return response;
        }

        if (to.isAfter(today.plusDays(MAX_DAYS - 1))) {
            response = new DialogResponse("Погоду можно посмотреть только на ближайшие "
                    + MAX_DAYS + " дней. Выберите другие даты.");
            return response;
        }

        String city = session.getCity();
        try {
            response = new DialogResponse(format(city, from, to, provider.getForecast(city, from, to)),
                    keyboard.build(session.getState()));
        } catch (IOException e) {
            response = new DialogResponse("Не удалось получить погоду. Попробуйте позже.",
                    keyboard.build(session.getState()));
        }


        return response;
    }

    /**
     * Формирует текст ответа: заголовок и по строке на каждый день.
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
     * Добавляется знак, для положительной температуры
     */
    private String signed(double temperature) {
        long rounded = Math.round(temperature);
        if (rounded > 0) {
            return "+" + rounded;
        }
        return String.valueOf(rounded);
    }
}