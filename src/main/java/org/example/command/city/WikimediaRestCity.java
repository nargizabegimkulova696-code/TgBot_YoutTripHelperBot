package org.example.command.city;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.example.StringUtilities;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс для работы с Wikimedia REST API
 */
public class WikimediaRestCity implements CityProvider {

    /**
     * Адрес api для получения краткой информации со страницы википедии (титульник + статус)
     */
    private static final String WIKI_SUMMARY_URL = "https://ru.wikipedia.org/api/rest_v1/page/summary/";

    /**
     * Адрес api для запроса id объекта
     */
    private static final String WIKI_CITY_ID_URL = "https://ru.wikipedia.org/w/api.php?" +
            "action=query&prop=pageprops&ppprop=wikibase_item&";

    /**
     * Адрес api, для поиска канонического названия объекта
     */
    private static final String WIKI_OPENSREACH_URL = "https://ru.wikipedia.org/w/api.php?" +
            "action=opensearch&limit=1&format=json&search=";

    /**
     * Адрес api WIKI_DATA
     */
    private static final String WIKI_DATA_URL = "https://query.wikidata.org/sparql?format=json&query=";

    /**
     * HttpClient для запросов к api
     */
    private final HttpClient httpClient = HttpClient.newBuilder().
            connectTimeout(Duration.ofSeconds(5)).
            build();

    /**
     * Хранит информацию о городах, найденных ранее
     */
    private final Map<String, City> catalog = new ConcurrentHashMap<>();

    /**
     * Парс json ответов в дерево
     */
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public City getInfo(String city) throws IOException {
        String normalTitle = StringUtilities.normalizeCityName(city);

        City visitedCities = catalog.get(normalTitle);

        if (visitedCities != null) {
            return visitedCities;
        }

        String canonCity = getCanonTitle(city);
        String cityId = findCityId(canonCity);

        if (isNotCity(cityId)) {
            throw new IOException("isNotCity, пользователь ввел не название города");
        }

        JsonNode root1 = get(WIKI_SUMMARY_URL +
                URLEncoder.encode(canonCity, StandardCharsets.UTF_8)
                        .replace("+", "%20"));

        String sparcl = "SELECT (SAMPLE(?pop) AS ?population) (SAMPLE(?ar) AS ?area) (SAMPLE(?inc) AS ?inception) "
                + "WHERE { wd:" + cityId + " wdt:P1082 ?pop . "
                + "OPTIONAL { wd:" + cityId + " wdt:P2046 ?ar . } "
                + "OPTIONAL { wd:" + cityId + " wdt:P571 ?inc . } }";

        JsonNode root2 = get(WIKI_DATA_URL +
                URLEncoder.encode(sparcl, StandardCharsets.UTF_8).replace("+", "%20"));

        City cityInfo = parseCity(root1, root2);

        catalog.put(normalTitle, cityInfo);

        return cityInfo;
    }

    /**
     * Проверка, что запрашиваемый объект является городом
     *
     * @param id - id города
     * @return - ложь если это город, истина, если не город
     */
    private boolean isNotCity(String id) throws IOException {
        String ask = "ASK { wd:" + id + " wdt:P31/wdt:P279* wd:Q515 }";

        JsonNode ans = get(WIKI_DATA_URL
                + URLEncoder.encode(ask, StandardCharsets.UTF_8).replace("+", "%20"));

        boolean bool = ans.path("boolean").asBoolean(false);

        return !bool;
    }

    /**
     * Каноническое название объекта, чтобы поиск id и города, в дальнейшем, был корректен
     */
    private String getCanonTitle(String obj) throws IOException {
        JsonNode root = get(WIKI_OPENSREACH_URL
                + URLEncoder.encode(obj, StandardCharsets.UTF_8).replace("+", "%20"));

        JsonNode title = root.path(1);

        if (!title.isArray() || title.isEmpty()) {
            throw new IOException("OpenSreach не нашел название объекта");
        }

        return title.get(0).asText();
    }

    /**
     * Поиск id объекта
     */
    private String findCityId(String obj) throws IOException {
        JsonNode root = get(WIKI_CITY_ID_URL +
                "titles=" + URLEncoder.encode(obj, StandardCharsets.UTF_8).replace("+", "%20")
                + "&redirects=1&format=json");

        String qId = "";
        JsonNode pages = root.path("query").path("pages");

        for (JsonNode page : pages) {
            qId = page.path("pageprops").path("wikibase_item").asText();
        }

        if (StringUtils.isBlank(qId)) {
            throw new IOException("Не удалось получить id для города");
        }

        return qId;
    }

    /**
     * Метод реализует запрос к api, для получения данных о городе
     *
     * @param url - ссылка, по которой создается запрос
     * @return JsonNode - возврат дерева узлов
     * @throws IOException - выброс ошибки, если данные не были получены либо запрос был прерван
     */
    private JsonNode get(String url) throws IOException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("User-Agent", "YourTripHelperBot/1.0")
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("Ошибка запроса к Wikimedia REST API" + response.statusCode());
            }
            return mapper.readTree(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Запрос к Wikimedia REST API прерван" + e);
        }
    }

    ;

    /**
     * Обработка дерева узлов в City
     *
     * @param root1 - корень дерева
     * @return CityInfo - карточка с информацией о городе
     */
    private City parseCity(JsonNode root1, JsonNode root2) {

        JsonNode row = root2.path("results").path("bindings").path(0);

        String inception = inceptionFormated(row.path("inception").path("value").asText());
        String population = populationFormated(row.path("population").path("value").asText());
        String area = row.path("area").path("value").asText("");

        String title = root1.path("title").asText();
        String description = root1.path("description").asText();
        String extract = root1.path("extract").asText();

        return new City(title, description, inception, population, area, extract);
    }

    /**
     * Метод форматирует год, так как он приходит в формате: 1831-01-01T00:00:00Z, отсюда нужны первые 4 цифры
     *
     * @param inception - дата основания
     * @return - отформатированная дата основания
     */
    private String inceptionFormated(String inception) {
        if (StringUtils.isBlank(inception)) {
            return "";
        }

        //Если перед числом "-", то год до н. э.
        boolean mns = inception.startsWith("-");

        String bodyYear = mns ? inception.substring(1) : inception;

        int idx = bodyYear.indexOf("-");
        if (idx < 0) {
            return "";
        }

        int year;

        try {
            year = Integer.parseInt(bodyYear.substring(0, idx));
        } catch (NumberFormatException e) {
            return "";
        }

        return mns ? year + " год до н.э." : year + " год";
    }

    /**
     * Форматирование данных о населении города из, например, 185362 в 185 362
     */
    private String populationFormated(String population) {
        if (StringUtils.isBlank(population)) {
            return "";
        }

        try {

            long pop = new BigDecimal(population).longValue();

            return String.format(Locale.forLanguageTag("ru"), "%,d", pop)
                    .replace('\u00A0', ' ')
                    .replace('\u202F', ' ');

        } catch (NumberFormatException e) {
            return "";
        }
    }
}
