package org.example.task4;

import org.example.command.city.City;
import org.example.command.city.CityProvider;
import org.example.command.city.CsvCatalogCity;
import org.example.handler.command.AboutCityHandler;
import org.example.handler.states.WaitingCityHandler;
import org.example.response.Button;
import org.example.response.DialogResponse;
import org.example.server.CityServer;
import org.example.session.BotSession;
import org.example.session.DialogState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

/**
 * Тестирование about_city и CityServer на логику работы с двумя источниками данных о городе
 */
public class AboutCityAndCityServerTest {
    /**
     * Сессия пользователя
     */
    private final BotSession session = new BotSession();

    /**
     * Фейковый провайдер вместо api
     */
    private final CityProvider provider = (city) -> {

        switch (city) {
            case "ВУЗ" -> {
                throw new IOException("Это не город");
            }

            case "Суздаль" -> {
                return new City("Суздаль", "", "", "", "", "");
            }

            case "Орск" -> {
                return new City("Орск", "город в Оренбургской области РФ",
                        "", "185 362", "", "");
            }

            case "Москва" -> {
                return new City("Москва", "", "", "", "", "");
            }

            default -> {
                return new City("", "", "", "", "", "");
            }
        }
    };

    /**
     * CSV каталог, для городов с населением >500тыс
     */
    private final String csv = "name;status;population;area;description;climate;facts\n"
            + "Москва;Столица России;13 млн;2 560 км²;Крупнейший город страны;умеренный;Метро|Кремль\n";

    /**
     * Обработчик выбора города в состоянии WAITING_FOR_CITY
     */
    private final WaitingCityHandler handler;

    /**
     * Обработчик команды about_city
     */
    private final AboutCityHandler command;

    /**
     * Конструктор инициализирует csv каталог, сервер, обработчиков команд
     */
    AboutCityAndCityServerTest() throws IOException {
        try (InputStreamReader input = new InputStreamReader(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)) {
            CityProvider catalog = new CsvCatalogCity(input);

            CityServer server = new CityServer(provider, catalog);

            handler = new WaitingCityHandler(server);
            command = new AboutCityHandler(server);
        }
        ;
    }

    /**
     * Тестирование ответа, если отправлено не название города
     */
    @Test
    void TestNotTitleCity() {
        DialogResponse response = handler.handle("ВУЗ", session);

        Assertions.assertEquals("Информации о городе не найдено", response.getAnswer());
        Assertions.assertFalse(response.hasButtons());
    }

    /**
     * Тестирование ответного сообщения, которое приходит пользователю
     * после выбора города, если он найден
     */
    @Test
    void testBotLogicKnownCity() {
        DialogResponse response = handler.handle("Москва", session);

        List<String> callbacks = response.getListOfCommands()
                .stream()
                .flatMap(Collection::stream)
                .map(Button::getCallBackData)
                .toList();

        Assertions.assertEquals("Выберете категорию для города Москва:", response.getAnswer());
        Assertions.assertEquals(List.of("cat:about_city", "cat:weather", "cat:food", "cat:culture", "cat:attractions", "cat:back"), callbacks);
    }

    /**
     * Логика обработки ответа в категории about_city не сломается даже если доступно одно поле
     */
    @Test
    void TestOnlyTitleOfCity() {
        session.setCity("Суздаль");
        session.setState(DialogState.CITY_SELECTED);

        DialogResponse response = command.execute(session);

        Assertions.assertEquals("Суздаль", response.getAnswer());
    }

    /**
     * Логика обработки ответа не ломается, еси доступно несколько полей, но не все
     */
    @Test
    void TestAnswerWithoutSomeBlocks() {
        session.setCity("Орск");
        session.setState(DialogState.CITY_SELECTED);

        DialogResponse response = command.execute(session);

        String expect = "Орск - город в Оренбургской области РФ\n\nНаселение - 185 362 человек";
        Assertions.assertEquals(expect, response.getAnswer());
    }

    /**
     * Если население города >500 тыс., то карточка города собирается от csvCatalog
     */
    @Test
    void TestProviderToCatalog() {
        session.setCity("Москва");
        session.setState(DialogState.CITY_SELECTED);

        DialogResponse response = command.execute(session);

        String expect = "Москва - Столица России\n\nНаселение: 13 млн\n\nПлощадь: 2 560 км²\n\n" +
                "Крупнейший город страны\n\nКлимат: умеренный\n\nИнтересные факты о городе: \n- Метро\n- Кремль";
        Assertions.assertEquals(expect, response.getAnswer());
    }

    /**
     * Если указано название города, но он не был найден
     */
    @Test
    void TestNotFoundCity() {
        DialogResponse response = handler.handle("Томск", session);
        Assertions.assertEquals("Информации о городе не найдено", response.getAnswer());
    }
}
