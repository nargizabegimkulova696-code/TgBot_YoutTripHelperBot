package org.example.Weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import java.net.URI;
import java.net.URLEncoder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.time.Duration;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Создание прогноза погоды на период указанных дат с помощью API Open-Meteo
 */
public class OpenMeteoWeather implements WeatherProvider {
    /**
     * Адрес API для поиска координат города
     */
    private final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";

    /**
     * Адрес API для запроса прогноза погоды
     */
    private final String FORECAST_URL = "https://api.open-meteo.com/v1/forecast";

    /**
     * HTTP-клиент для запросов к API: погоды и координат города, если соединения нет в течение 5 секунд -
     * выбрасываем ошибку подключения
     */
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /**
     * Класс ObjectMapper разбирает JSON-ответы от Open-Meteo в дерево, которое состоит из узлов
     */
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<Day> getForecast(String city, LocalDate from, LocalDate to) throws IOException {
        JsonNode place = findPlace(city); //findPlace(city) возвращает объект с данными о городе

        //указываем часовой пояс для запроса прогноза погоды
        String timezone = place.path("timezone").asText("auto");

        // API + широта + долгота + температура макс + температура мин
        // + погодный код + первая дата периода + последняя дата периода
        JsonNode forecast = get(FORECAST_URL
                + "?latitude=" + place.path("latitude").asDouble()
                + "&longitude=" + place.path("longitude").asDouble()
                + "&daily=temperature_2m_max,temperature_2m_min,weather_code"
                + "&timezone=" + URLEncoder.encode(timezone, StandardCharsets.UTF_8)
                + "&start_date=" + from
                + "&end_date=" + to);

        return parseDaily(forecast);
    }

    /**
     * Поиск данных о городе по его названию.
     * @param city название города
     * @return JSON-узел найденного места, содержит: name, latitude, longitude и timezone
     * @throws IOException если город не найден или запрос к геокодеру не удался
     */
    private JsonNode findPlace(String city) throws IOException {
        JsonNode root = get(GEOCODING_URL
                + "?name=" + URLEncoder.encode(city, StandardCharsets.UTF_8)
                + "&count=1&language=ru&countryCode=RU"); //возвращает тело оберкти на http запросом

        JsonNode results = root.path("results");
        if (!results.isArray() || results.isEmpty()) {
            throw new IOException("Город не найден в геокодере: " + city);
        }

        //значение поля results массив, состоящий из одного элемента - объекта данных о городе
        return results.get(0);
    }


    /**
     * Выполняет GET-запрос по указанному адресу, ответ в формате JSON.
     * @param url полный адрес запроса вместе с параметрами
     * @return узел, который JSON-ответ: геокодера или forcast
     * @throws IOException ошибка при работе с API (возникла сетевая ошибка или запрос был прерван)
     */
    private JsonNode get(String url) throws IOException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("Код ошибки Open-Meteo " + response.statusCode());
            }
            return mapper.readTree(response.body());
        } catch (InterruptedException e) {
            //java выключает флаг остановки потока, включаем обратно
            Thread.currentThread().interrupt();
            throw new IOException("Запрос к Open-Meteo прерван", e);
        }
    }

    /**
     * Разбирает поле "daily" ответа от Open-Meteo по запросу к forecast в список дней.
     * В "daily" хранятся поля (день, мак-темп, мин-темп), а их значение это массивы, где номер i элемента относится к i дню
     * @param root корневой JSON-узел, ответ от api по запросу к forecast
     * @return список дней
     */
    public List<Day> parseDaily(JsonNode root) throws IOException {
        //объект, который хранит значение прогноза погоды на каждый день в периоде указанных дат
        JsonNode daily = root.path("daily");

        JsonNode dates = daily.path("time");
        JsonNode maxTemps = daily.path("temperature_2m_max");
        JsonNode minTemps = daily.path("temperature_2m_min");
        JsonNode codes = daily.path("weather_code");

        if (!dates.isArray() || dates.size() != maxTemps.size()
                || dates.size() != minTemps.size() || dates.size() != codes.size()) {
            throw new IOException("Ответ от Open-Meteo не содержит один вид или несколько из видов данных");
        }

        List<Day> days = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) {
            if (maxTemps.get(i).isNull() || minTemps.get(i).isNull() || codes.get(i).isNull()) {
                // Какого-то вида данных нет на этот день, поэтому он не будет учитываться
                continue;
            }
            days.add(new Day(LocalDate.parse(
                    dates.get(i).asText()),
                    minTemps.get(i).asDouble(),
                    maxTemps.get(i).asDouble(),
                    describe(codes.get(i).asInt())));
        }
        return days;
    }

    /**
     * Переводит код погоды в краткое описание
     */
    public String describe(int code) {
        return switch (code) {
            case 0 -> "ясно";
            case 1 -> "преимущественно ясно";
            case 2 -> "переменная облачность";
            case 3 -> "пасмурно";
            case 45 -> "туман";
            case 48 -> "изморозь, туман";
            case 51 -> "слабая морось";
            case 53 -> "морось";
            case 55 -> "сильная морось";
            case 56, 57 -> "ледяная морось";
            case 61 -> "слабый дождь";
            case 63 -> "дождь";
            case 65 -> "сильный дождь";
            case 66, 67 -> "ледяной дождь";
            case 71 -> "слабый снег";
            case 73 -> "снег";
            case 75 -> "сильный снег";
            case 77 -> "снежная крупа";
            case 80 -> "слабый ливень";
            case 81 -> "ливень";
            case 82 -> "сильный ливень";
            case 85 -> "слабый снегопад";
            case 86 -> "сильный снегопад";
            case 95 -> "гроза";
            case 96, 99 -> "гроза с градом";
            default -> "без описания";
        };
    }
}