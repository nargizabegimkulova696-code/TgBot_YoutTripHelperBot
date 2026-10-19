package org.example;

import org.example.Catalog.CityCatalog;
import org.example.Catalog.CsvCityCatalog;
import org.example.DialogService.DialogResponse;
import org.example.SearchCityByName.CityValidator;
import org.example.Session.BotSession;
import org.example.Session.DialogState;
import org.example.Session.SessionStorage;
import org.example.Weather.WeatherDialog;
import org.example.Weather.WeatherProvider;
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
 * Тесты: отдельные сессии пользователей ({@link SessionStorage}),
 * Команды /help, /about_city и /weather, которые обрабатывает {@link BotLogic}
 * Прогноз погоды проверяется через {@link WeatherDialog}
 */
class SessionsAndCommandsTest {
    /**
     * Тестовый каталог
     */
    private final String CSV = "name;status;population;area;description;climate;facts\n"
            + "Москва;Столица России;13 млн;2 560 км²;Крупнейший город страны;умеренный;Метро|Кремль\n"
            + "Тула;;;;;;\n";

    /**
     * Хранилище сессий
     */
    private final SessionStorage storage = new SessionStorage();

    /**
     * Проверяемая логика бота
     */
    private final BotLogic logic;

    /**
     * Собирает логику с тестовым каталогом
     */
    SessionsAndCommandsTest() throws IOException {
        CityCatalog catalog = new CsvCityCatalog(new InputStreamReader(
                new ByteArrayInputStream(CSV.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        logic = new BotLogic(storage, new CityValidator(catalog));
    }

    /**
     * Два пользователя выбирают разные города, и города не смешиваются
     */
    @Test
    void testSessionsAreSeparate() {
        logic.makeMessage("Москва", 1L);
        logic.makeMessage("Тула", 2L);

        Assertions.assertEquals("Москва", storage.getSession(1L).getCity());
        Assertions.assertEquals("Тула", storage.getSession(2L).getCity());
    }

    /**
     * После /start одного пользователя не сбрасывает диалог другого
     */
    @Test
    void testStartOfOneDoesNotResetAnother() {
        logic.makeMessage("Москва", 1L);
        logic.makeMessage("Тула", 2L);

        logic.makeMessage("/start", 1L);

        Assertions.assertEquals(DialogState.WAITING_FOR_CITY, storage.getSession(1L).getState());
        Assertions.assertEquals(DialogState.CITY_SELECTED, storage.getSession(2L).getState());
        Assertions.assertEquals("Тула", storage.getSession(2L).getCity());
    }

    /**
     * /about_city показывает название и другие данные из каталога
     */
    @Test
    void testAboutCityShowsCatalog() {
        logic.makeMessage("Москва", 1L);

        String text = logic.makeMessage("cat:about_city", 1L).getAnswer();

        String expected = "Москва — Столица России\n\n"
                + "Население: 13 млн\nПлощадь: 2 560 км²\n\n"
                + "Крупнейший город страны\n\n"
                + "Климат: умеренный\n\n"
                + "Интересные факты:\n— Метро\n— Кремль";
        Assertions.assertEquals(expected, text);
    }

    /**
     * Если у города нет данных, то показывается только название
     */
    @Test
    void testThatCityIsOutcast() {
        logic.makeMessage("Тула", 1L);

        Assertions.assertEquals("Тула", logic.makeMessage("cat:about_city", 1L).getAnswer());
    }

    /**
     * /weather до выбора города просит выбрать город
     */
    @Test
    void testWeatherWithoutCityAsksForCity() {
        DialogResponse response = logic.makeMessage("cat:weather", 1L);

        Assertions.assertEquals("Сначала выберите город. Напишите его название.", response.getAnswer());
        Assertions.assertEquals(DialogState.WAITING_FOR_CITY, storage.getSession(1L).getState());
    }

    /**
     * Если неверный формат даты, то ответ и бот остаётся в режиме погоды
     */
    @Test
    void testWeatherRejectsWrongFormat() {
        logic.makeMessage("Москва", 1L);
        logic.makeMessage("cat:weather", 1L);

        DialogResponse response = logic.makeMessage("завтра", 1L);

        Assertions.assertEquals("Некорректный ввод.\n " +
                        "Напишите период в формате дд.мм - дд.мм, например 12.10 - 18.10.",
                response.getAnswer());
        Assertions.assertEquals(DialogState.CAT_WEATHER, storage.getSession(1L).getState());
    }

    /**
     * Период длиннее 14 дней отклоняетс
     */
    @Test
    void testWeatherRejectsTooLongPeriod() {
        logic.makeMessage("Москва", 1L);
        logic.makeMessage("cat:weather", 1L);

        DialogResponse response = logic.makeMessage("01.01 - 31.12", 1L);

        Assertions.assertEquals("Период слишком длинный. Выберите не больше 14 дней.", response.getAnswer());
    }

    /**
     * Формат прогноза проверяем на fake-WeatherProvider
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
        String answer = new WeatherDialog(provider).handleDates(dm + " - " + dm, session).getAnswer();

        Assertions.assertEquals("Погода в городе Москва с " + dm + " по " + dm + ":\n"
                + dm + ": -1...+7°C, дождь", answer);
    }
}
