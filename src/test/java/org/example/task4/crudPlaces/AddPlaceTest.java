package org.example.task4.crudPlaces;

import org.example.command.places.Place;
import org.example.command.places.PlacesProvider;
import org.example.handler.ButtonHandler;
import org.example.handler.StateHandler;
import org.example.handler.command.crudPlaces.AddPlaceHandler;
import org.example.handler.states.crudPlaces.AddPlaceStateHandler;
import org.example.response.Button;
import org.example.response.DialogResponse;
import org.example.server.TripServer;
import org.example.session.BotSession;
import org.example.session.DialogState;
import org.example.task4.FakeStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

/**
 * Тестирование кнопки "com:add_place" и обработчика состояния ADD_PLACE
 */
public class AddPlaceTest {
    /**
     * Сессия пользователя
     */
    private final BotSession session = new BotSession(1L);

    /**
     * Обработчик кнопки "com:add_place"
     */
    private final ButtonHandler buttonHandler = new AddPlaceHandler();

    /**
     * Тестовый источник вместо API
     */
    private final PlacesProvider provider = (city, tag) -> {
        if (!tag.equals("tag:bakeries")) {
            return List.of();
        }
        return List.of(
                new Place("Французский пекарь", "ул. Ленина, 1"),
                new Place("Дом Хлеба", "ул. Тургенева, 4"),
                new Place("Поль Бейкери", "ул. Ленина, 5"),
                new Place("Добрый пекарь", "ул. Московская, 6")
        );
    };

    /**
     * Сервер для запросов к фейковому Api мест и фейковой бд
     */
    private final TripServer server = new TripServer(provider, new FakeStorage());

    /**
     * Обработчик состояния ADD_PLACE
     */
    private final StateHandler stateHandler = new AddPlaceStateHandler(server);

    /**
     * Город, тег и категория выбраны
     */
    AddPlaceTest() {
        session.setCity("Москва");
        session.setState(DialogState.CATEGORY_FOOD);
        session.setCategory("food");
        session.setTag("tag:bakeries");
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
     * Тестирование кнопки, если пользователь выбрал тег и категорию
     */
    @Test
    void CatAndTagChosenTest() {
        DialogResponse response = buttonHandler.execute(session);

        Assertions.assertEquals("Введите название места, " +
                "которое хотите добавить, в формате: Название, Адрес", response.getAnswer());
        Assertions.assertEquals(DialogState.ADD_PLACE, session.getState());
        Assertions.assertEquals(List.of("cat:back"), callbacks(response));
    }

    /**
     * Тестирование кнопки, если пользователь не выбрал тег и категорию
     */
    @Test
    void CatAndTagNotChosenTest() {
        session.setCategory(null);
        session.setTag(null);

        DialogResponse response = buttonHandler.execute(session);

        Assertions.assertEquals("Сначала выберете категорию и тег, чтобы добавить место)"
                , response.getAnswer());
        Assertions.assertFalse(response.hasButtons());
    }

    /**
     * Тестирование некорректного формата ввода
     */
    @Test
    void WrongInputTest() {
        session.setState(DialogState.ADD_PLACE);

        DialogResponse response = stateHandler.handle("Пекарня", session);

        Assertions.assertEquals("Некорректный формат ввода", response.getAnswer());
        Assertions.assertEquals(List.of("cat:back"), callbacks(response));
    }

    /**
     * Тестирование корректного формата ввода
     */
    @Test
    void RightInputTest() {
        session.setState(DialogState.ADD_PLACE);

        DialogResponse response = stateHandler.handle("Пекарня 2, ул. Гагарина 10а", session);

        Assertions.assertEquals("Добавляю в список место: Пекарня 2, ул. Гагарина 10а 🥨",
                response.getAnswer());
        Assertions.assertEquals(List.of("com:save", "com:cancel"), callbacks(response));
    }

    /**
     * Тестирование ответного сообщение при выборе сохранить
     */
    @Test
    void SaveChooseTest() {
        session.setState(DialogState.ADD_PLACE);
        stateHandler.handle("Пекарня 2, ул. Гагарина 10а", session);

        DialogResponse response = stateHandler.handle("com:save", session);

        Assertions.assertEquals("""
                Место успешно добавлено в список
                
                Места, где можно вкусно покушать:
                
                1) Французский пекарь, ул. Ленина, 1
                
                2) Дом Хлеба, ул. Тургенева, 4
                
                3) Поль Бейкери, ул. Ленина, 5
                
                4) Добрый пекарь, ул. Московская, 6
                
                5) Пекарня 2, ул. Гагарина 10а 🥨
                
                Страница 1 из 1""", response.getAnswer());
        Assertions.assertEquals(DialogState.IN_TAG_FOOD, session.getState());
        Assertions.assertEquals(List.of("com:add_place", "com:edit_place", "com:delete_place",
                "cat:back"), callbacks(response));
    }

    /**
     * Тестирование ответного сообщение при выборе отменить
     */
    @Test
    void CancelChooseTest() {
        session.setState(DialogState.ADD_PLACE);
        stateHandler.handle("Пекарня 2, ул. Гагарина 10а", session);

        DialogResponse response = stateHandler.handle("com:cancel", session);

        Assertions.assertEquals("""
                Место не добавлено в список
                
                Места, где можно вкусно покушать:
                
                1) Французский пекарь, ул. Ленина, 1
                
                2) Дом Хлеба, ул. Тургенева, 4
                
                3) Поль Бейкери, ул. Ленина, 5
                
                4) Добрый пекарь, ул. Московская, 6
                
                Страница 1 из 1""", response.getAnswer());
        Assertions.assertEquals(DialogState.IN_TAG_FOOD, session.getState());
        Assertions.assertEquals(List.of("com:add_place", "cat:back"), callbacks(response));
    }
}
