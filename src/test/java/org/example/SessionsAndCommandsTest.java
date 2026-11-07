package org.example;

import org.example.command.city.City;
import org.example.command.city.CityProvider;
import org.example.command.city.CsvCatalogCity;
import org.example.command.weather.WeatherProvider;
import org.example.handler.states.WeatherDatesHandler;
import org.example.response.DialogResponse;
import org.example.server.CityServer;
import org.example.server.TripServer;
import org.example.session.BotSession;
import org.example.session.DialogState;
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
 * Тесты: отдельные сессии пользователей.
 * Команда /help и /weather, которые обрабатывает {@link BotLogic}
 * Прогноз погоды проверяется через {@link WeatherDatesHandler}
 */
class SessionsAndCommandsTest {
    /**
     * Каталог заглушка
     */
    private final String CSV = ";;;;;;\n"
            + ";;;;;;\n"
            + ";;;;;;\n";

    /**
     * Проверяемая логика бота и сессии, которая создается внутри логики
     */
    private final BotLogic logic;

    /**
     * Инициализация логики, каталога и сервера
     */
    SessionsAndCommandsTest() throws IOException {
        try (InputStreamReader reader = new InputStreamReader(
                new ByteArrayInputStream(CSV.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)) {

            CityProvider catalog = new CsvCatalogCity(reader);
            CityProvider provider = (city) -> {
                if (city.equals("Москва")) {
                    return new City("Москва", "", "", "", "", "");
                }
                return new City("Тула", "", "", "", "", "");
            };

            CityServer server = new CityServer(provider, catalog);

            logic = new BotLogic(server,
                    new TripServer((city, tag) -> List.of()),
                    (city, from, to) -> List.of());
        }
        ;
    }

    /**
     * Два пользователя выбирают разные города, и города не смешиваются
     */
    @Test
    void testSessionsAreSeparate() {
        logic.makeMessage("Москва", 1L);
        logic.makeMessage("Тула", 2L);

        Assertions.assertTrue(logic.makeMessage("cat:about_city", 1L).getAnswer().startsWith("Москва"));
        Assertions.assertEquals("Тула", logic.makeMessage("cat:about_city", 2L).getAnswer());
    }

    /**
     * После /start одного пользователя не сбрасывает диалог другого
     */
    @Test
    void testStartOfOneDoesNotResetAnother() {
        logic.makeMessage("Москва", 1L);
        logic.makeMessage("Тула", 2L);

        logic.makeMessage("/start", 1L);

        Assertions.assertEquals("Выберете категорию для города Москва:",
                logic.makeMessage("Москва", 1L).getAnswer());
        Assertions.assertEquals("Тула", logic.makeMessage("cat:about_city", 2L).getAnswer());
    }

    /**
     * Weather до выбора города просит выбрать город
     */
    @Test
    void testWeatherWithoutCityAsksForCity() {
        DialogResponse response = logic.makeMessage("cat:weather", 1L);

        Assertions.assertEquals("Сначала выберите город, для этого напишите его название)", response.getAnswer());
        Assertions.assertEquals("Выберете категорию для города Москва:",
                logic.makeMessage("Москва", 1L).getAnswer());
    }

    /**
     * Если неверный формат даты, то пользователь остаётся в категории погоды
     */
    @Test
    void testWeatherRejectsWrongFormat() {
        logic.makeMessage("Москва", 1L);
        logic.makeMessage("cat:weather", 1L);

        String expected = "Некорректный ввод.\n " +
                "Напишите период в формате дд.мм - дд.мм, например 12.10 - 18.10.";

        Assertions.assertEquals(expected, logic.makeMessage("завтра", 1L).getAnswer());
        Assertions.assertEquals(expected, logic.makeMessage("послезавтра", 1L).getAnswer());
    }

    /**
     * Период длиннее 14 дней отклоняется
     */
    @Test
    void testWeatherRejectsTooLongPeriod() {
        logic.makeMessage("Москва", 1L);
        logic.makeMessage("cat:weather", 1L);

        DialogResponse response = logic.makeMessage("01.01 - 31.12", 1L);

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

        BotSession session = new BotSession();
        session.setCity("Москва");
        session.setState(DialogState.CAT_WEATHER);

        String dm = DateTimeFormatter.ofPattern("dd.MM").format(day);
        String answer = new WeatherDatesHandler(provider).handle(dm + " - " + dm, session).getAnswer();

        Assertions.assertEquals("Погода в городе Москва с " + dm + " по " + dm + ":\n"
                + dm + ": -1...+7°C, дождь", answer);
    }
}