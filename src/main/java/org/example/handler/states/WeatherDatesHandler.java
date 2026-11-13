package org.example.handler.states;

import org.example.command.weather.WeatherProvider;
import org.example.handler.StateHandler;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.session.BotSession;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Состояние CAT_WEATHER: разбирает введённый период дат и возвращает прогноз погоды
 */
public class WeatherDatesHandler implements StateHandler {
    /**
     * Максимальная длина периода прогноза, включая сегодня.
     */
    public static final int MAX_DAYS = 14;

    /**
     * Период в формате регулярного выражения "дд.мм - дд.мм".
     * Между датами допускаются дефис и пробелы вокруг них.
     */
    private final Pattern PERIOD = Pattern.compile("(\\d{1,2})\\.(\\d{1,2})\\s*[-]\\s*(\\d{1,2})\\.(\\d{1,2})");

    /**
     * Формат вывода даты в ответе бота
     */
    private static final DateTimeFormatter DAY_MONTH = DateTimeFormatter.ofPattern("dd.MM");

    /**
     * Ответ, который получит пользователь, если введет неверный формат даты
     */
    private static final String WRONG_INPUT = "Некорректный ввод.\n " +
            "Напишите период в формате дд.мм - дд.мм, например 12.10 - 18.10.";

    /**
     * Источник прогноза погоды, чтобы класс функционировал без подключения API
     */
    private final WeatherProvider provider;

    /**
     * Кнопки с категориями
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    /**
     * Инициализация WeatherDatesHandler от provider
     */
    public WeatherDatesHandler(WeatherProvider provider) {
        this.provider = provider;
    }

    /**
     * Обработка введённого периода дат.
     * Если ввод корректный - возвращает прогноз, иначе просит ввести заново
     *
     * @param text    сообщение пользователя в формате дд.мм - дд.мм
     * @param session сессия пользователя
     * @return ответ от бота (текстовое сообщение + inline-клавиатура)
     */
    @Override
    public DialogResponse handle(String text, BotSession session) {
        Matcher matcher = PERIOD.matcher(text.trim());
        if (!matcher.matches()) {
            return new DialogResponse(WRONG_INPUT,
                    keyboard.build(session.getState()));
        }

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
            return new DialogResponse("Такой даты не существует. Напишите период в формате дд.мм - дд.мм.",
                    keyboard.build(session.getState()));
        }

        if (ChronoUnit.DAYS.between(from, to) + 1 > MAX_DAYS) {
            return new DialogResponse("Период слишком длинный. Выберите не больше " + MAX_DAYS + " дней.",
                    keyboard.build(session.getState()));
        }

        if (to.isAfter(today.plusDays(MAX_DAYS - 1))) {
            return new DialogResponse("Погоду можно посмотреть только на ближайшие "
                    + MAX_DAYS + " дней. Выберите другие даты.",
                    keyboard.build(session.getState()));
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
        StringBuilder text = new StringBuilder("Погода в городе " + city
                + " с " + from.format(DAY_MONTH) + " по " + to.format(DAY_MONTH) + ":");

        for (WeatherProvider.Day day : days) {
            text.append("\n").append(day.date().format(DAY_MONTH)).append(": ")
                    .append(signed(day.minTemp())).append("...").append(signed(day.maxTemp())).append("°C, ")
                    .append(day.description());
        }
        return text.toString();
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