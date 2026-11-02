package org.example;

import org.example.handler.ButtonHandler;
import org.example.handler.command.AttractionsHandler;
import org.example.handler.command.CultureHandler;
import org.example.handler.command.FoodHandler;
import org.example.response.Button;
import org.example.response.DialogResponse;
import org.example.session.BotSession;
import org.example.session.DialogState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Тестирование логики обработки команд и кнопок: food, culture, attractions
 */
public class PlacesByCategoryTest {
    /**
     * Сессия пользователя
     */
    private final BotSession session = new BotSession();

    /**
     * Словарь, содержащий обработчиков команд и кнопок
     */
    private final Map<String, ButtonHandler> handlers = Map.of(
            "cat:food", new FoodHandler(),
            "cat:culture", new CultureHandler(),
            "cat:attractions", new AttractionsHandler(),

            "/food", new FoodHandler(),
            "/culture", new CultureHandler(),
            "/attractions", new AttractionsHandler()
    );

    /**
     * Метод возвращает список из callback от кнопок
     *
     * @param response - сообщение, из которого берем список кнопок
     */
    List<String> callbacks(DialogResponse response) {
        return response
                .getListOfCommands()
                .stream()
                .flatMap(Collection::stream)
                .map(Button::getCallBackData)
                .toList();
    }

    /**
     * Тестирование ответного сообщения при выборе категории еда
     */
    @Test
    void TestFoodHandlerButton() {
        session.setCity("Москва");
        session.setState(DialogState.CITY_SELECTED);

        DialogResponse response = handlers.get("cat:food").execute(session);

        Assertions.assertEquals("Что вас интересует?", response.getAnswer());
        Assertions.assertEquals(List.of("tag:restaurants",
                "tag:cafe",
                "tag:bakeries",
                "tag:pizzerias",
                "tag:fastfood",
                "cat:back"), callbacks(response));
        Assertions.assertEquals(DialogState.CAT_FOOD, session.getState());
    }

    /**
     * Тестирование ответного сообщения при выборе категории культура
     */
    @Test
    void TestCultureHandlerButton() {
        session.setCity("Екатеринбург");
        session.setState(DialogState.CITY_SELECTED);

        DialogResponse response = handlers.get("cat:culture").execute(session);

        Assertions.assertEquals("Что вас интересует?", response.getAnswer());
        Assertions.assertEquals(List.of("tag:museums",
                "tag:temples",
                "tag:historical_buildings",
                "tag:monuments",
                "tag:theaters", "cat:back"), callbacks(response));
        Assertions.assertEquals(DialogState.CAT_CULTURE, session.getState());
    }

    /**
     * Тестирование ответного сообщения при выборе категории развлечения
     */
    @Test
    void TestAttractionsHandlerButton() {
        session.setCity("Челябинск");
        session.setState(DialogState.CITY_SELECTED);

        DialogResponse response = handlers.get("cat:attractions").execute(session);

        Assertions.assertEquals("Что вас интересует?", response.getAnswer());
        Assertions.assertEquals(List.of(
                "tag:shopping",
                "tag:attraction_parks",
                "tag:movie",
                "tag:zoos",
                "tag:nature",
                "cat:back"
        ), callbacks(response));
        Assertions.assertEquals(DialogState.CAT_ATTRACTIONS, session.getState());
    }

    /**
     * Тестирование вызова команд еда, культура, развлечения, находясь в не выбранном городе
     */
    @ParameterizedTest
    @ValueSource(strings = {"/food", "/culture", "/attractions"})
    void TestHandlerCommandWithNullCity(String command) {
        DialogResponse response = handlers.get(command).execute(session);

        Assertions.assertEquals("Сначала выберите город, для этого напишите его название)", response.getAnswer());
        Assertions.assertEquals(DialogState.WAITING_FOR_CITY, session.getState());
    }

    /**
     * Тестирования вызова команд еда, культура, развлечения, находясь в других категориях или тегах
     *
     * @param city          - текущий город
     * @param state         - состояние, в котором находимся сейчас
     * @param command       - команда, которую вызываем
     * @param expectedState - ожидаемая смена состояния
     */
    @ParameterizedTest
    @CsvSource({
            "Москва, CAT_ABOUT_CITY, /food, CAT_FOOD",
            "Челябинск, CAT_WEATHER, /culture, CAT_CULTURE",
            "Томск, IN_TAG_FOOD, /attractions, CAT_ATTRACTIONS"
    })
    void TestFoodCommandsInDiffsStates(String city, DialogState state, String command, DialogState expectedState) {
        session.setCity(city);
        session.setState(state);

        DialogResponse response = handlers.get(command).execute(session);

        Assertions.assertEquals("Что вас интересует?", response.getAnswer());
        Assertions.assertEquals(expectedState, session.getState());
    }
}
