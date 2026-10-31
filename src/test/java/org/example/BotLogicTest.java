package org.example;

import org.example.catalog.CityCatalog;
import org.example.catalog.CsvCityCatalog;
import org.example.response.Button;
import org.example.response.DialogResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;


/**
 * Тестирование BotLogic на корректное ответное сообщение пользователю
 * и переход между командами
 */
class BotLogicTest {
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
     * Сообщение, в котором бот говорит о неверном использовании бота
     */
    private final String WRONG_INTERACTIONS = "Чтобы воспользоваться ботом, нажми на нужную кнопку, " +
            "чтобы получить информацию или нажми back, чтобы вернуться к выбору города.";

    /**
     * Тестовый каталог
     */
    private final String CSV = "name;status;population;area;description;climate;facts\n"
            + "Москва;Столица России;13 млн;2 560 км²;Крупнейший город страны;умеренный;Метро|Кремль\n"
            + "Тула;;;;;;\n";

    /**
     * Логика бота
     */
    private final BotLogic logic;


    /**
     * Инициализация каталога и логики бота
     */
    BotLogicTest() throws IOException {

        try (InputStreamReader reader = new InputStreamReader(
                new ByteArrayInputStream(CSV.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)) {

            CityCatalog catalog = new CsvCityCatalog(reader);
            CityServer server = new CityServer(catalog);
            logic = new BotLogic(server, (city, from, to) -> List.of());
        }
    }

    /**
     * Тестирование ответного сообщения, которое приходит пользователю
     * после выбора города, если он найден в каталоге
     */
    @Test
    void testBotLogicKnownCity() {
        DialogResponse response = logic.makeMessage("москва", CHAT);
        List<String> callbacks = response.getListOfCommands()
                .stream()
                .flatMap(Collection::stream)
                .map(Button::getCallBackData)
                .toList();

        Assertions.assertEquals("Выберете категорию для города Москва:", response.getAnswer());
        Assertions.assertEquals(List.of("cat:about_city", "cat:weather", "cat:back"), callbacks);
    }

    /**
     * Тестирование ответного сообщения, которое приходит пользователю
     * после выбора города, если он не найден в каталоге
     */
    @Test
    void testBotLogicUnKnownCity() {
        DialogResponse response = logic.makeMessage("Томск", CHAT);

        Assertions.assertEquals("Информации о городе не найдено", response.getAnswer());
        Assertions.assertFalse(response.hasButtons());
    }

    /**
     * Команда /start спрашивает название города, ответ должен содержать: текст + пустой список категорий
     */
    @Test
    void testStartAsksWithoutAny() {
        DialogResponse response = logic.makeMessage("/start", CHAT);

        Assertions.assertEquals(START_MESSAGE, response.getAnswer());
        Assertions.assertFalse(response.hasButtons());
    }

    /**
     * Тестирование ответного сообщения на некорректное сообщение пользователя,
     * если бот находится в ожидании выбора категории
     */
    @Test
    void TestForWrongInteractionsInCitySelected() {
        logic.makeMessage("Москва", CHAT);
        DialogResponse response = logic.makeMessage("something", CHAT);

        Assertions.assertEquals(WRONG_INTERACTIONS, response.getAnswer());
    }

    /**
     * Тестирование ответного сообщения на некорректное сообщение пользователя,
     * когда бот находится в состоянии CAT_ABOUT_CITY
     */
    @Test
    void TestForWrongInteractionsInCatAboutCity() {
        logic.makeMessage("Москва", CHAT);
        logic.makeMessage("cat:about_city", CHAT);

        DialogResponse response = logic.makeMessage("something", CHAT);

        Assertions.assertEquals(WRONG_INTERACTIONS, response.getAnswer());
    }


    /**
     * Тестирование логики бота на корректный переход между командами
     */
    @Test
    void testSwitchBetweenCommands() {
        logic.makeMessage("Москва", CHAT);

        logic.makeMessage("cat:weather", CHAT);
        // мы в категории погоды: на мусорный ввод отвечает погодный диалог
        Assertions.assertEquals("Некорректный ввод.\n " +
                        "Напишите период в формате дд.мм - дд.мм, например 12.10 - 18.10.",
                logic.makeMessage("завтра", CHAT).getAnswer());

        // сразу переходим в about_city, не возвращаясь в меню
        String about = logic.makeMessage("cat:about_city", CHAT).getAnswer();
        Assertions.assertEquals("Москва - Столица России" +
                "\n\nНаселение: 13 млн" +
                "\n\nПлощадь: 2 560 км²" +
                "\n\nКрупнейший город страны" +
                "\n\nКлимат: умеренный" +
                "\n\nИнтересные факты о городе: " +
                "\n- Метро" +
                "\n- Кремль", about);
    }
}
