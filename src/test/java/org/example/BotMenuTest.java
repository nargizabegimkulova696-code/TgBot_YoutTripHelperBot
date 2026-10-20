package org.example;

import org.example.Catalog.CityCatalog;
import org.example.Catalog.CsvCityCatalog;
import org.example.DialogService.Button;
import org.example.DialogService.DialogResponse;
import org.example.DialogService.KeyboardFactory;
import org.example.SearchCityByName.CityValidator;
import org.example.Session.DialogState;
import org.example.Session.SessionStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Тесты: кнопок, доступных после выбора города ({@link KeyboardFactory})и команды /start.
 * Логику ответного сообщения обрабатывает {@link BotLogic}
 */
class BotMenuTest {
    /**
     * Id чата тестового пользователя
     */
    private final long CHAT = 1L;

    /**
     * Сообщение, в которым бот просит выбрать город
     */
    private final String START_MESSAGE =
            "Привет, я бот — помощник в путешествиях. В какой город вы хотели бы отправиться?";

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
     * Логика бота, которую тестируем
     */
    private final BotLogic logic;

    /**
     * Инициализация каталога и логики бота
     */
    BotMenuTest() throws IOException {
        CityCatalog catalog = new CsvCityCatalog(new InputStreamReader(
                new ByteArrayInputStream(CSV.getBytes()), StandardCharsets.UTF_8));
        logic = new BotLogic(storage, new CityValidator(catalog));
    }

    /**
     * После выбора города доступно три кнопки
     */
    @Test
    void testThreeCommandsInMenu() {
        List<List<Button>> keyboard = new KeyboardFactory().build(DialogState.CITY_SELECTED);

        List<String> data = new ArrayList<>();

        for (List<Button> row : keyboard) {
            for (Button button : row) {
                String callback = button.getCallBackData();
                data.add(callback);
            }
        }

        Assertions.assertEquals(List.of("cat:about_city", "cat:weather", "cat:back"), data);
    }

    /**
     * Пока город не выбран, список категорий пуст
     */
    @Test
    void testMenuWaitingForCity() {
        Assertions.assertTrue(new KeyboardFactory().build(DialogState.WAITING_FOR_CITY).isEmpty());
    }

    /**
     * /start спрашивает город и не показывает список категорий
     */
    @Test
    void testStartAsksWithoutAny() {
        DialogResponse response = logic.makeMessage("/start", CHAT);

        Assertions.assertEquals(START_MESSAGE, response.getAnswer());
        Assertions.assertFalse(response.hasButtons());
    }

    /**
     * Если город лежит в каталоге, то в ответном сообщении лежит текст+список категорий
     */
    @Test
    void testValidCityShowsMenu() {
        DialogResponse response = logic.makeMessage("Москва", CHAT);

        Assertions.assertEquals("Выберете категорию для города Москва:", response.getAnswer());
        Assertions.assertTrue(response.hasButtons());
        Assertions.assertEquals(DialogState.CITY_SELECTED, storage.getSession(CHAT).getState());
    }

    /**
     * Если город не найден в каталоге, то в ответном сообщении содержится только текст
     */
    @Test
    void testSituationWithStrangeCity() {
        DialogResponse response = logic.makeMessage("Атлантида", CHAT);

        Assertions.assertEquals("Информации о городе не найдено", response.getAnswer());
        Assertions.assertEquals(DialogState.WAITING_FOR_CITY, storage.getSession(CHAT).getState());
    }

    /**
     * Кнопка back возвращает в меню, кнопка back - к выбору города
     */
    @Test
    void testBackStepByStep() {
        logic.makeMessage("Москва", CHAT);
        logic.makeMessage("cat:about_city", CHAT);

        DialogResponse toMenu = logic.makeMessage("cat:back", CHAT);
        Assertions.assertEquals(DialogState.CITY_SELECTED, storage.getSession(CHAT).getState());
        Assertions.assertEquals(3, toMenu.getListOfCommands().size());

        DialogResponse toCity = logic.makeMessage("cat:back", CHAT);
        Assertions.assertEquals(DialogState.WAITING_FOR_CITY, storage.getSession(CHAT).getState());
        Assertions.assertEquals(START_MESSAGE, toCity.getAnswer());
    }

    /**
     * В будущем будет команда /back, которая не должна портить логику ответного сообщения,
     * если пользователь находится в меню
     */
    @Test
    void testBackInStateWaitingForCity() {
        DialogResponse response = Assertions.assertDoesNotThrow(() -> logic.makeMessage("cat:back", CHAT));

        Assertions.assertEquals(START_MESSAGE, response.getAnswer());
        Assertions.assertEquals(DialogState.WAITING_FOR_CITY, storage.getSession(CHAT).getState());
    }

    /**
     * Из одной команды можно сразу перейти в другую, не возвращаясь к списку категорий
     */
    @Test
    void testSwitchBetweenCommands() {
        logic.makeMessage("Москва", CHAT);

        logic.makeMessage("cat:weather", CHAT);
        Assertions.assertEquals(DialogState.CAT_WEATHER, storage.getSession(CHAT).getState());

        logic.makeMessage("cat:about_city", CHAT);
        Assertions.assertEquals(DialogState.CAT_ABOUT_CITY, storage.getSession(CHAT).getState());
    }
}
