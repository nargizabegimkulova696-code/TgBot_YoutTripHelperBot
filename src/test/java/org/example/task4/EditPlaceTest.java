package org.example.task4;

import org.example.command.places.Place;
import org.example.command.places.PlacesProvider;
import org.example.handler.StateHandler;
import org.example.handler.states.crudPlaces.AddPlaceStateHandler;
import org.example.handler.states.crudPlaces.EditPlaceStateHandler;
import org.example.response.Button;
import org.example.response.DialogResponse;
import org.example.server.TripServer;
import org.example.session.BotSession;
import org.example.session.DialogState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collection;
import java.util.List;

/**
 * Тестирование обработчика состояния EDIT_PLACE, CHANGE_NAME, CHANGE_ADDRESS
 */
public class EditPlaceTest {
    /**
     * Сессия пользователя
     */
    private final BotSession session = new BotSession(1L);

    /**
     * Тестовый источник вместо API
     */
    private final PlacesProvider provider = (city, tag) -> {
        if (!tag.equals("tag:bakeries")) {
            return List.of();
        }
        return List.of(
                new Place("Французский пекарь", "ул. Ленина, 1"),
                new Place("Поль Бейкери", "ул. Ленина, 5"),
                new Place("Добрый пекарь", "ул. Московская, 6")
        );
    };

    /**
     * Сервер для запросов к фейковому Api мест и фейковой бд
     */
    private final TripServer server = new TripServer(provider, new FakeStorage());

    /**
     * Обработчик состояние ADD_PLACE
     */
    private final StateHandler addHandler = new AddPlaceStateHandler(server);

    /**
     * Обработчик состояния EDIT_PLACE
     */
    private final StateHandler editHandler = new EditPlaceStateHandler(server);

    /**
     * Город, тег и категория выбраны, места добавлены
     */
    EditPlaceTest() {
        session.setCity("Москва");

        session.setState(DialogState.CATEGORY_FOOD);
        session.setCategory("food");
        session.setTag("tag:bakeries");

        session.setState(DialogState.ADD_PLACE);
        addHandler.handle("Пекарня 2, ул. Гагарина 10а", session);
        addHandler.handle("com:save", session);

        session.setState(DialogState.ADD_PLACE);
        addHandler.handle("Булочки, ул. Ленина 20б", session);
        addHandler.handle("com:save", session);
    }

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
     * Тестирование граничных случаев ввода номера
     *
     * @param input  - ввод
     * @param answer - ответ от бота
     */
    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "1|Вы не можете редактировать места, которые были в списке изначально. Выберете номер места, которое вы добавили в список.",
            "6|Вы не можете редактировать места, которые были в списке изначально. Выберете номер места, которое вы добавили в список.",
            "-1|Вы не можете редактировать места, которые были в списке изначально. Выберете номер места, которое вы добавили в список.",
            "Французский пекарь|Чтобы изменить место, нужно ввести его номер"
    })
    void WrongInputTest(String input, String answer) {
        session.setState(DialogState.EDIT_PLACE);

        DialogResponse response = editHandler.handle(input, session);

        Assertions.assertEquals(answer, response.getAnswer());
        Assertions.assertEquals(List.of("cat:back"), callbacks(response));
    }

    /**
     * Тестирование корректного ввода номера места
     */
    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "4|4) Пекарня 2, ул. Гагарина 10а 🥨\\nЧто хотите изменить название или адрес места?",
            "5|5) Булочки, ул. Ленина 20б 🥨\\nЧто хотите изменить название или адрес места?"
    })
    void RightInputTest(String input, String answer) {
        session.setState(DialogState.EDIT_PLACE);

        DialogResponse response = editHandler.handle(input, session);

        Assertions.assertEquals(answer.replace("\\n", "\n"), response.getAnswer());
    }

    /**
     * Тестирование просьбы ввести новое название или адреса места
     */
    @ParameterizedTest
    @CsvSource({
            "4, Название, Введите новое название, CHANGE_NAME",
            "5, Адрес, Введите новый адрес, CHANGE_ADDRESS"
    })
    void RequestTest(String num, String ans, String req, DialogState state) {
        session.setState(DialogState.EDIT_PLACE);
        editHandler.handle(num, session);

        DialogResponse response = editHandler.handle(ans, session);

        Assertions.assertEquals(req, response.getAnswer());
        Assertions.assertEquals(state, session.getState());
    }

    /**
     * Тестирование промежуточного результата перед применением изменений
     */
    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "4|Название|Юный Пекарь|Юный Пекарь, ул. Гагарина 10а 🥨\\nВыберете:",
            "5|Адрес|ул. 8 марта 192а|Булочки, ул. 8 марта 192а 🥨\\nВыберете:"
    })
    void IntermediateResultTest(String num, String choose, String change, String expected) {
        session.setState(DialogState.EDIT_PLACE);
        editHandler.handle(num, session);
        editHandler.handle(choose, session);

        DialogResponse response = editHandler.handle(change, session);

        Assertions.assertEquals(expected.replace("\\n", "\n"), response.getAnswer());
        Assertions.assertEquals(List.of("com:save", "com:cancel"), callbacks(response));
        Assertions.assertEquals(DialogState.EDIT_PLACE, session.getState());
    }

    /**
     * Тестирование конечного результат при выборе com:save
     * (пользователь изменил название места)
     */
    @Test
    void FinalResultSaveTest() {
        session.setState(DialogState.EDIT_PLACE);
        editHandler.handle("4", session);
        editHandler.handle("название", session);
        editHandler.handle("Юный Пекарь", session);

        DialogResponse response = editHandler.handle("com:save", session);

        Assertions.assertEquals(DialogState.IN_TAG_FOOD, session.getState());
        Assertions.assertEquals("""
                Место успешно изменено
                
                1) Французский пекарь, ул. Ленина, 1
                
                2) Поль Бейкери, ул. Ленина, 5
                
                3) Добрый пекарь, ул. Московская, 6
                
                4) Юный Пекарь, ул. Гагарина 10а 🥨
                
                5) Булочки, ул. Ленина 20б 🥨
                
                Страница 1 из 1""", response.getAnswer());
        Assertions.assertEquals(List.of("com:add_place", "com:edit_place", "com:delete_place",
                "cat:back"), callbacks(response));
    }

    /**
     * Тестирование конечного результат при выборе com:cancel
     * (пользователь отменил изменения адреса места)
     */
    @Test
    void FinalResultCancelTest() {
        session.setState(DialogState.EDIT_PLACE);
        editHandler.handle("5", session);
        editHandler.handle("адрес", session);
        editHandler.handle("ул. 8 марта 192а", session);

        DialogResponse response = editHandler.handle("com:cancel", session);

        Assertions.assertEquals(DialogState.IN_TAG_FOOD, session.getState());
        Assertions.assertEquals("""
                Место не изменено
                
                1) Французский пекарь, ул. Ленина, 1
                
                2) Поль Бейкери, ул. Ленина, 5
                
                3) Добрый пекарь, ул. Московская, 6
                
                4) Пекарня 2, ул. Гагарина 10а 🥨
                
                5) Булочки, ул. Ленина 20б 🥨
                
                Страница 1 из 1""", response.getAnswer());
        Assertions.assertEquals(List.of("com:add_place", "com:edit_place", "com:delete_place",
                "cat:back"), callbacks(response));
    }
}
