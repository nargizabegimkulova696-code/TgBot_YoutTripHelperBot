package org.example.command.places;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Получает данные о местах через API Geoapify.
 * Сначала по названию города находит place_id (Geocoding API),
 * а потом ищет места нужной категории внутри границ города (Places API)
 */
public class GeoapifyPlaces implements PlacesProvider {
    /**
     * Адрес API для поиска города
     */
    private static final String GEOCODING_URL = "https://api.geoapify.com/v1/geocode/search";

    /**
     * Адрес API для поиска мест
     */
    private static final String PLACES_URL = "https://api.geoapify.com/v2/places";

    /**
     * Количество мест за один раз
     */
    private static final int LIMIT = 50;

    /**
     * Ключ доступа к Geoapify
     */
    private final String apiKey;

    /**
     * Теги категорий, соответсвующие Geoapify, читается из файла geoapify_tags.properties
     */
    private final Properties categories = new Properties();

    /**
     * Уже загруженные списки мест: ключ - "город|тег", значение - места.
     * Нужен для пейджинга: при нажатии more список берётся отсюда, а не запрашивается заново
     */
    private final Map<String, List<Place>> tagsPlaces = new ConcurrentHashMap<>();

    /**
     * HTTP-клиент для запросов к API
     */
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /**
     * Разбор JSON-ответов
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Создание источника мест с нужными категориями из "geoapify_tags.properties"
     *
     * @param apiKey ключ Geoapify
     * @throws IOException если файл с соответствием тегов не найден
     */
    public GeoapifyPlaces(String apiKey) throws IOException {
        this.apiKey = apiKey;

        try (InputStream input = getClass().getResourceAsStream("/geoapify_tags.properties")) {
            if (input == null) {
                throw new IOException("Файл geoapify_tags.properties не найден");
            }
            categories.load(input);
        }
    }

    @Override
    public List<Place> getPlaces(String city, String tag) throws IOException {
        String key = city + "|" + tag;
        List<Place> recievedPlaces = tagsPlaces.get(key);
        if (recievedPlaces != null) {
            return recievedPlaces;
        }

        String category = categories.getProperty(tag.replace("tag:", ""));
        if (category == null) {
            return List.of();
        }

        JsonNode root = get(PLACES_URL
                + "?categories=" + category
                + "&filter=place:" + findCityId(city)
                + "&limit=" + LIMIT
                + "&lang=ru"
                + "&apiKey=" + apiKey);

        List<Place> places = parsePlaces(root);
        tagsPlaces.put(key, places);
        return places;
    }

    /**
     * Поиск идентификатора города
     */
    private String findCityId(String city) throws IOException {
        JsonNode root = get(GEOCODING_URL
                + "?text=" + URLEncoder.encode(city, StandardCharsets.UTF_8)
                + "&type=city&filter=countrycode:ru&lang=ru&format=json"
                + "&apiKey=" + apiKey);

        JsonNode results = root.path("results");
        if (!results.isArray() || results.isEmpty()) {
            throw new IOException("Город не найден в Geoapify: " + city);
        }
        return results.get(0).path("place_id").asText();
    }


    /**
     * GET-запрос, ответ в формате JSON по указаному адресу
     *
     * @param url полный адрес запроса вместе с параметрами
     * @return узел JSON-ответ
     * @throws IOException ошибка при работе с API (возникла сетевая ошибка или запрос был прерван)
     */
    private JsonNode get(String url) throws IOException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("Код ошибки Geoapify " + response.statusCode());
            }
            return mapper.readTree(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Запрос к Geoapify прерван", e);
        }
    }

    /**
     * Разбор ответа Places API.
     * Из каждого элемента features берется название и адрес места
     *
     * @param root JSON-ответ Places API
     * @return список мест
     */
    public List<Place> parsePlaces(JsonNode root) {
        List<Place> places = new ArrayList<>();

        for (JsonNode feature : root.path("features")) {
            JsonNode properties = feature.path("properties");
            String name = properties.path("name").asText("");
            if (name.isBlank()) {
                continue;
            }

            String street = properties.path("street").asText("");
            String house = properties.path("housenumber").asText("");
            String address = house.isBlank() ? street : street + ", " + house;

            places.add(new Place(name, address));
        }
        return places;
    }
}
