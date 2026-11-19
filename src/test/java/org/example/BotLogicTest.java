package org.example;

import org.example.catalog.city.CityCatalog;
import org.example.catalog.city.CsvCityCatalog;
import org.example.command.weather.WeatherProvider;
import org.example.handler.command.*;
import org.example.response.Button;
import org.example.response.DialogResponse;
import org.example.service.CityService;
import org.example.service.TripService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * Тестирование публичного интерфейса BotLogic, что бот отвечает пользователю
 */
class BotLogicTest {
    /**
     * Id чата тестового пользователя
     */
    private final long CHAT_ID = 1L;

    /**
     * Id чата второго тестового пользователя
     */
    private final long OTHER_CHAT_ID = 2L;

    /**
     * Сообщение, в которым бот просит выбрать город
     */
    private static final String START_MESSAGE =
            "Привет, я бот — помощник в путешествиях. В какой город вы хотели бы отправиться?";

    /**
     * Каталог городов, из которого бот берёт данные в тестах:
     * Москва с полным описанием и Тула без описания, только название
     */
    private static final String CITY_CATALOG = """
            name;status;population;area;description;climate;facts
            Москва;Столица России;13 млн;2 560 км²;Крупнейший город страны;умеренный;Метро|Кремль
            Тула;;;;;;
            """;

    /**
     * Подсказка, которую бот присылает, если пользователь написал текст там, где ждёт нажатия кнопки
     */
    private static final String USE_BUTTONS_HINT = "Чтобы воспользоваться ботом, нажми на нужную кнопку, " +
            "чтобы получить информацию или нажми back, чтобы вернуться к выбору города.";
    /**
     * Текст, который бот присылает по кнопке about_city для Москвы
     */
    private static final String MOSCOW_INFO = "Москва - Столица России"
            + "\n\nНаселение: 13 млн"
            + "\n\nПлощадь: 2 560 км²"
            + "\n\nКрупнейший город страны"
            + "\n\nКлимат: умеренный"
            + "\n\nИнтересные факты о городе: "
            + "\n- Метро"
            + "\n- Кремль";

    /**
     * Кнопки, которые видит пользователь сразу после выбора города
     */
    private final List<List<Button>> citySelectedButtons = List.of(
            List.of(new Button("о городе", AboutCityHandler.CALLBACK)),
            List.of(new Button("погода", WeatherHandler.CALLBACK)),
            List.of(new Button("еда", FoodHandler.CALLBACK)),
            List.of(new Button("культура", CultureHandler.CALLBACK)),
            List.of(new Button("развлечения", AttractionsHandler.CALLBACK)),
            List.of(new Button("назад", BackHandler.CALLBACK)));
    /**
     * Кнопки, которые видит пользователь в категории about_city
     */
    private final List<List<Button>> aboutCityButtons = List.of(
            List.of(new Button("погода", WeatherHandler.CALLBACK)),
            List.of(new Button("еда", FoodHandler.CALLBACK)),
            List.of(new Button("культура", CultureHandler.CALLBACK)),
            List.of(new Button("развлечения", AttractionsHandler.CALLBACK)),
            List.of(new Button("назад", BackHandler.CALLBACK)));

    /**
     * Кнопки, которые видит пользователь в категории weather
     */
    private final List<List<Button>> weatherButtons = List.of(
            List.of(new Button("о городе", AboutCityHandler.CALLBACK)),
            List.of(new Button("еда", FoodHandler.CALLBACK)),
            List.of(new Button("культура", CultureHandler.CALLBACK)),
            List.of(new Button("развлечения", AttractionsHandler.CALLBACK)),
            List.of(new Button("назад", BackHandler.CALLBACK)));

    /**
     * Доступ к каталогу городов из CITY_CATALOG
     */
    private final CityService cityService;

    /**
     * Логика бота
     */
    private final BotLogic logic;

    /**
     * Сервис категорий food, culture, attractions
     */
    private final TripService tripService = new TripService((city, tag) -> List.of());

    /**
     * Инициализация каталога и логики бота
     */
    BotLogicTest() throws IOException {

        try (InputStreamReader reader = new InputStreamReader(
                new ByteArrayInputStream(CITY_CATALOG.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)) {

            CityCatalog catalog = new CsvCityCatalog(reader);
            cityService = new CityService(catalog);
            logic = new BotLogic(cityService, tripService, (city, from, to) -> List.of());
        }
    }

    /**
     * Тестирование ответного сообщения, которое приходит пользователю
     * после выбора города, если он найден в каталоге
     */
    @Test
    void testKnownCityShowsCategories() {
        DialogResponse response = logic.makeMessage("москва", CHAT_ID);

        Assertions.assertEquals("Выберете категорию для города Москва:", response.getAnswer());
        Assertions.assertEquals(citySelectedButtons, response.getListOfCommands());
    }

    /**
     * Тестирование ответного сообщения, которое приходит пользователю
     * после выбора города, если он не найден в каталоге
     */
    @Test
    void testUnknownCityShowsNotFound() {
        DialogResponse response = logic.makeMessage("Томск", CHAT_ID);

        Assertions.assertEquals("Информации о городе не найдено", response.getAnswer());
        Assertions.assertFalse(response.hasButtons());
    }

    /**
     * Команда /start спрашивает название города,
     * ответ должен содержать: текст + пустой список категорий
     */
    @Test
    void testStartAsksWithoutAny() {
        DialogResponse response = logic.makeMessage("/start", CHAT_ID);

        Assertions.assertEquals(START_MESSAGE, response.getAnswer());
        Assertions.assertFalse(response.hasButtons());
    }

    /**
     * Тестирование ответного сообщения на некорректное сообщение пользователя,
     * если бот находится в ожидании выбора категории
     */
    @Test
    void TestForWrongInteractionsInCitySelected() {
        logic.makeMessage("Москва", CHAT_ID);

        DialogResponse response = logic.makeMessage("something", CHAT_ID);

        Assertions.assertEquals(USE_BUTTONS_HINT, response.getAnswer());
        Assertions.assertEquals(citySelectedButtons, response.getListOfCommands());
    }

    /**
     * Тестирование ответного сообщения на некорректное сообщение пользователя,
     * когда бот находится в состоянии CAT_ABOUT_CITY
     */
    @Test
    void TestForWrongInteractionsInCatAboutCity() {
        logic.makeMessage("Москва", CHAT_ID);
        logic.makeMessage(AboutCityHandler.CALLBACK, CHAT_ID);

        DialogResponse response = logic.makeMessage("something", CHAT_ID);

        Assertions.assertEquals(USE_BUTTONS_HINT, response.getAnswer());
        Assertions.assertEquals(aboutCityButtons, response.getListOfCommands());
    }


    /**
     * Тестирование перехода из категории weather сразу в about_city без возврата в меню:
     * текст карточки города + кнопки категории about_city
     */
    @Test
    void testSwitchBetweenCommands() {
        logic.makeMessage("Москва", CHAT_ID);
        logic.makeMessage(WeatherHandler.CALLBACK, CHAT_ID);

        DialogResponse response = logic.makeMessage(AboutCityHandler.CALLBACK, CHAT_ID);

        Assertions.assertEquals(MOSCOW_INFO, response.getAnswer());
        Assertions.assertEquals(aboutCityButtons, response.getListOfCommands());
    }

    /**
     * Два пользователя выбирают разные города, и города не смешиваются
     */
    @Test
    void testSessionsAreSeparate() {
        logic.makeMessage("Москва", CHAT_ID);
        logic.makeMessage("Тула", OTHER_CHAT_ID);

        Assertions.assertEquals(MOSCOW_INFO, logic.makeMessage(AboutCityHandler.CALLBACK, CHAT_ID).getAnswer());
        Assertions.assertEquals("Тула", logic.makeMessage(AboutCityHandler.CALLBACK, OTHER_CHAT_ID).getAnswer());
    }

    /**
     * После /start одного пользователя не сбрасывает диалог другого
     */
    @Test
    void testStartOfOneDoesNotResetAnother() {
        logic.makeMessage("Москва", CHAT_ID);
        logic.makeMessage("Тула", OTHER_CHAT_ID);

        logic.makeMessage("/start", CHAT_ID);

        Assertions.assertEquals("Выберете категорию для города Москва:",
                logic.makeMessage("Москва", CHAT_ID).getAnswer());
        Assertions.assertEquals("Тула", logic.makeMessage(AboutCityHandler.CALLBACK, OTHER_CHAT_ID).getAnswer());
    }


    /**
     * About_city показывает название и другие данные из каталога
     */
    @Test
    void testAboutCityShowsCatalog() {
        logic.makeMessage("Москва", CHAT_ID);

        String text = logic.makeMessage(AboutCityHandler.CALLBACK, CHAT_ID).getAnswer();

        Assertions.assertEquals(MOSCOW_INFO, text);
    }

    /**
     * Если о городе нет данных, то показано только название
     */
    @Test
    void testThatCityIsOutcast() {
        logic.makeMessage("Тула", CHAT_ID);

        Assertions.assertEquals("Тула", logic.makeMessage(AboutCityHandler.CALLBACK, CHAT_ID).getAnswer());
    }

    /**
     * Weather до выбора города просит выбрать город
     */
    @Test
    void testWeatherWithoutCityAsksForCity() {
        DialogResponse response = logic.makeMessage(WeatherHandler.CALLBACK, CHAT_ID);

        Assertions.assertEquals("Сначала выберите город, для этого напишите его название)", response.getAnswer());
        Assertions.assertEquals("Выберете категорию для города Москва:",
                logic.makeMessage("Москва", CHAT_ID).getAnswer());
    }

    /**
     * Если неверный формат даты, то пользователь остаётся в категории погоды
     */
    @Test
    void testWeatherRejectsWrongFormat() {
        logic.makeMessage("Москва", CHAT_ID);
        logic.makeMessage(WeatherHandler.CALLBACK, CHAT_ID);

        String expected = "Некорректный ввод.\n " +
                "Напишите период в формате дд.мм - дд.мм, например 12.10 - 18.10.";

        Assertions.assertEquals(expected, logic.makeMessage("завтра", CHAT_ID).getAnswer());
        Assertions.assertEquals(expected, logic.makeMessage("послезавтра", CHAT_ID).getAnswer());
    }

    /**
     * Период длиннее 14 дней отклоняется
     */
    @Test
    void testWeatherRejectsTooLongPeriod() {
        logic.makeMessage("Москва", CHAT_ID);
        logic.makeMessage(WeatherHandler.CALLBACK, CHAT_ID);

        DialogResponse response = logic.makeMessage("01.01 - 31.12", CHAT_ID);

        Assertions.assertEquals("Период слишком длинный. Выберите не больше 14 дней.", response.getAnswer());
    }

    /**
     * Формат ответного сообщения прогноза погоды, который проверяется на fake-WeatherProvider
     */
    @Test
    void testWeatherFormatsForecast() {
        LocalDate day = LocalDate.now().plusDays(1);

        WeatherProvider provider = (city, from, to) ->
                List.of(new WeatherProvider.Day(day, -1.0, 7.0, "дождь"));

        BotLogic logicWithForecast = new BotLogic(cityService, tripService, provider);
        logicWithForecast.makeMessage("Москва", CHAT_ID);
        logicWithForecast.makeMessage(WeatherHandler.CALLBACK, CHAT_ID);

        String dm = DateTimeFormatter.ofPattern("dd.MM").format(day);
        String answer = logicWithForecast.makeMessage(dm + " - " + dm, CHAT_ID).getAnswer();

        Assertions.assertEquals("Погода в городе Москва с " + dm + " по " + dm + ":\n"
                + dm + ": -1...+7°C, дождь", answer);
    }

    /**
     * Если источник погоды недоступен, то пользователь получает сообщение об ошибке
     */
    @Test
    void testWeatherShowsErrorWhenProviderFails() {
        WeatherProvider brokenProvider = (city, from, to) -> {
            throw new IOException("нет сети");
        };

        BotLogic logicWithoutWeather = new BotLogic(cityService, tripService, brokenProvider);
        logicWithoutWeather.makeMessage("Москва", CHAT_ID);
        logicWithoutWeather.makeMessage(WeatherHandler.CALLBACK, CHAT_ID);

        String dm = DateTimeFormatter.ofPattern("dd.MM").format(LocalDate.now().plusDays(1));
        String answer = logicWithoutWeather.makeMessage(dm + " - " + dm, CHAT_ID).getAnswer();

        Assertions.assertEquals("Не удалось получить погоду. Попробуйте позже.", answer);
    }
}
