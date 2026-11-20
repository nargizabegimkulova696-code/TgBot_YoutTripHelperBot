package org.example.task4;

import org.example.command.places.Place;
import org.example.command.places.PlacesProvider;
import org.example.handler.StateHandler;
import org.example.handler.states.TagHandler;
import org.example.handler.states.crudPlaces.AddPlaceStateHandler;
import org.example.handler.states.crudPlaces.DeletePlaceStateHandler;
import org.example.response.Button;
import org.example.response.DialogResponse;
import org.example.server.TripServer;
import org.example.session.BotSession;
import org.example.session.DialogState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

/**
 * Тестирование обработчика состояния DELETE_PLACE
 */
public class DeletePlaceTest {
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
     * Обработчик состояний тегов
     */
    private final TagHandler tagHandler = new TagHandler(server);

    /**
     * Обработчик состояния ADD_PLACE
     */
    private final StateHandler addHandler = new AddPlaceStateHandler(server);

    /**
     * Обработчик состояния
     */
    private final StateHandler deleteHandler = new DeletePlaceStateHandler(server);
    //убрать подхачиванние так нельзя
    /**
     * Город, тег и категория выбраны, места добавлены
     */
    DeletePlaceTest() {
        session.setCity("Москва");

        session.setState(DialogState.CATEGORY_FOOD);
        session.setCategory("food");
        session.setTag("tag:bakeries");

        session.setState(DialogState.ADD_PLACE);
        addHandler.handle("Дом Булочек, ул. Гагарина 10а", session);
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
     * Тестирование ответного сообщение, если пользователь добавлял ранее места,
     * то ему доступны кнопки: редактирования и удаления места
     */
    @Test
    void MessageTest() {
        DialogResponse response = tagHandler.handle("tag:bakeries", session);

        Assertions.assertEquals("""
                Места, где можно вкусно покушать:
                
                1) Французский пекарь, ул. Ленина, 1
                
                2) Поль Бейкери, ул. Ленина, 5
                
                3) Добрый пекарь, ул. Московская, 6
                
                4) Дом Булочек, ул. Гагарина 10а 🥨
                
                Страница 1 из 1""", response.getAnswer());
        Assertions.assertEquals(List.of("com:add_place", "com:edit_place",
                "com:delete_place", "cat:back"), callbacks(response));
        Assertions.assertEquals(DialogState.IN_TAG_FOOD, session.getState());
    }

    /**
     * Тестирование корректного ввода номера места
     * (граничные случаи были протестированы в EditPlaceTest)
     */
    @Test
    void RightInputTest() {
        session.setState(DialogState.DELETE_PLACE);

        DialogResponse response = deleteHandler.handle("4", session);

        Assertions.assertEquals("""
                4) Дом Булочек, ул. Гагарина 10а 🥨
                Точно ли вы хотите удалить это место?""", response.getAnswer());
        Assertions.assertEquals(List.of("com:save", "com:cancel"), callbacks(response));
    }

    /**
     * Тестирование при выборе удаления места
     * (пользователю больше не доступны кнопки edit_place и delete_place)
     */
    @Test
    void FinalSaveResultTest() {
        session.setState(DialogState.DELETE_PLACE);
        deleteHandler.handle("4", session);

        DialogResponse response = deleteHandler.handle("com:save", session);

        Assertions.assertEquals("""
                Место успешно удалено
                
                1) Французский пекарь, ул. Ленина, 1
                
                2) Поль Бейкери, ул. Ленина, 5
                
                3) Добрый пекарь, ул. Московская, 6
                
                Страница 1 из 1""", response.getAnswer());

        Assertions.assertEquals(List.of("com:add_place", "cat:back"), callbacks(response));
        Assertions.assertEquals(DialogState.IN_TAG_FOOD, session.getState());
    }

    /**
     * Тестирование при выборе отмены удаления места
     * (пользователю остаются доступны кнопки edit_place и delete_place)
     */
    @Test
    void FinalCancelResultTest() {
        session.setState(DialogState.DELETE_PLACE);
        deleteHandler.handle("4", session);

        DialogResponse response = deleteHandler.handle("com:cancel", session);

        Assertions.assertEquals("""
                Место не удалено
                
                1) Французский пекарь, ул. Ленина, 1
                
                2) Поль Бейкери, ул. Ленина, 5
                
                3) Добрый пекарь, ул. Московская, 6
                
                4) Дом Булочек, ул. Гагарина 10а 🥨
                
                Страница 1 из 1""", response.getAnswer());

        Assertions.assertEquals(List.of("com:add_place", "com:edit_place",
                "com:delete_place", "cat:back"), callbacks(response));
        Assertions.assertEquals(DialogState.IN_TAG_FOOD, session.getState());
    }

    /**
     * Тестирование, если при удалении одного места из нескольких добавленных,
     * с остальными остается все в порядке
     */
    @Test
    void FinalSavePlacesTest() {
        session.setState(DialogState.ADD_PLACE);
        addHandler.handle("Дом Хлеба, ул. Пионерская 15а", session);
        addHandler.handle("com:save", session);

        session.setState(DialogState.ADD_PLACE);
        addHandler.handle("Выпечка у дома, ул. Ленина 156а", session);
        addHandler.handle("com:save", session);

        session.setState(DialogState.DELETE_PLACE);
        deleteHandler.handle("5", session);

        DialogResponse response = deleteHandler.handle("com:save", session);

        Assertions.assertEquals("""
                Место успешно удалено
                
                1) Французский пекарь, ул. Ленина, 1
                
                2) Поль Бейкери, ул. Ленина, 5
                
                3) Добрый пекарь, ул. Московская, 6
                
                4) Дом Булочек, ул. Гагарина 10а 🥨
                
                5) Выпечка у дома, ул. Ленина 156а 🥨
                
                Страница 1 из 1""", response.getAnswer());

        Assertions.assertEquals(List.of("com:add_place", "com:edit_place",
                "com:delete_place", "cat:back"), callbacks(response));
        Assertions.assertEquals(DialogState.IN_TAG_FOOD, session.getState());
    }
}
