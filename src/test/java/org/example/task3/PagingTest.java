package org.example.task3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.command.places.GeoapifyPlaces;
import org.example.command.places.Place;
import org.example.command.places.PlacesProvider;
import org.example.handler.states.TagHandler;
import org.example.paging.PageCallback;
import org.example.response.Button;
import org.example.response.DialogResponse;
import org.example.server.TripServer;
import org.example.session.BotSession;
import org.example.session.DialogState;
import org.example.storage.UserPlaceStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

/**
 * Тестирование мест по тегам: пейджинг, кнопки "ещё" и "назад" и разбор ответа Geoapify.
 * Вместо API используется тестовый источник мест
 */
public class PagingTest {
    /**
     * Тестовый источник вместо API
     */
    private final PlacesProvider provider = (city, tag) -> {
        if (tag.equals("tag:movie")) {
            throw new IOException("API нет");
        }
        if (!tag.equals("tag:cafe")) {
            return List.of();
        }
        return List.of(
                new Place("Кафе 1", "ул. Ленина, 1"),
                new Place("Кафе 2", "ул. Ленина, 2"),
                new Place("Кафе 3", ""),
                new Place("Кафетерий 4", "ул. Тургенева, 4"),
                new Place("Кафе 5", "ул. Ленина, 5"),
                new Place("Кафе 6", "ул. Московская, 6"),
                new Place("Кафе 7", "ул. 8 марта, 8"));
    };
    /**
     * Обработчик тегов с тестовым источником мест
     */
    private final TagHandler handler = new TagHandler(new TripServer(provider,
            new UserPlaceStorage("jdbc:sqlite::memory:")));
    /**
     * Сессия пользователя
     */
    private final BotSession session = new BotSession(1);

    /**
     * Город и категория выбраны
     */
    PagingTest() {
        session.setCity("Казань");
        session.setState(DialogState.CAT_FOOD);
    }

    /**
     * Callback всех кнопок ответа по порядку
     */
    private List<String> callbacks(DialogResponse response) {
        return response.getListOfCommands().stream()
                .flatMap(List::stream)
                .map(Button::getCallBackData)
                .toList();
    }

    /**
     * Первая страница: 5 мест, кнопки "ещё" и "назад"
     */
    @Test
    void testFirstPage() {
        DialogResponse response = handler.handle("tag:cafe", session);

        Assertions.assertEquals("""
                Места, где можно вкусно покушать:
                
                1. Кафе 1, ул. Ленина, 1
                
                2. Кафе 2, ул. Ленина, 2
                
                3. Кафе 3
                
                4. Кафетерий 4, ул. Тургенева, 4
                
                5. Кафе 5, ул. Ленина, 5
                
                Страница 1 из 2""", response.getAnswer());
        Assertions.assertEquals("page:tag:cafe:1", response.getListOfCommands().get(0).get(0).getCallBackData());
        Assertions.assertEquals("cat:back", response.getListOfCommands().get(1).get(0).getCallBackData());
        Assertions.assertEquals(DialogState.IN_TAG_FOOD, session.getState());
    }

    /**
     * Последняя страница: кнопки "ещё" нет, "назад" ведёт на первую страницу
     */
    @Test
    void testLastPage() {
        handler.handle("tag:cafe", session);
        DialogResponse response = handler.handle("page:tag:cafe:1", session);

        Assertions.assertEquals("""
                Места, где можно вкусно покушать:
                
                7. Кафе 6, ул. Московская, 6
                
                8. Кафе 7, ул. 8 марта, 8
                
                Страница 2 из 2""", response.getAnswer());
        Assertions.assertEquals(1, response.getListOfCommands().size());
        Assertions.assertEquals("page:tag:cafe:0", response.getListOfCommands().get(0).get(0).getCallBackData());
    }

    /**
     * В теге нет мест: выводится сообщение об этом и только кнопка "назад" к тегам
     */
    @Test
    void testEmptyTagShowsNotFound() {
        DialogResponse response = handler.handle("tag:bakeries", session);

        Assertions.assertEquals("Места для тега не найдены(", response.getAnswer());
        Assertions.assertEquals(List.of("cat:back"), callbacks(response));
        Assertions.assertEquals(DialogState.IN_TAG_FOOD, session.getState());
    }

    /**
     * Источник мест не ответил при ошибке API.
     * Бот выводит сообщение и кнопку "назад"
     */
    @Test
    void testApiErrorShowsMessage() {
        DialogResponse response = handler.handle("tag:movie", session);

        Assertions.assertEquals("Места для тега не найдены(", response.getAnswer());
        Assertions.assertEquals(List.of("cat:back"), callbacks(response));
    }

    /**
     * Вместо кнопки прислали текст:
     * Выводится подсказка, состояние не меняется, кнопки тегов остаются
     */
    @Test
    void testTextInsteadOfButtonShowsHint() {
        DialogResponse response = handler.handle("привет", session);

        Assertions.assertEquals("Выберите, что вас интересует, с помощью кнопок.", response.getAnswer());
        Assertions.assertEquals(DialogState.CAT_FOOD, session.getState());
        Assertions.assertEquals(List.of("tag:restaurants", "tag:cafe", "tag:bakeries", "tag:pizzerias",
                "tag:fastfood", "cat:back"), callbacks(response));
    }


    /**
     * Разбор callback:
     * кнопка тега - страница 0, кнопка страницы - тег и номер
     */
    @Test
    void testParseCallback() {
        Assertions.assertEquals("page:tag:cafe:0", new PageCallback("tag:cafe").toData());
        Assertions.assertEquals("page:tag:cafe:2", new PageCallback("page:tag:cafe:2").toData());
    }

    /**
     * Тег, которого нет в geoapify_tags.properties
     */
    @Test
    void testUnknownTagInGeoapifyGivesEmptyList() throws IOException {
        Assertions.assertEquals(List.of(),
                new GeoapifyPlaces("test-key").getPlaces("Казань", "tag:unknown"));
    }

    /**
     * Разбор ответа Geoapify:
     * Место без названия пропускается, адрес собирается из улицы и дома
     */
    @Test
    void testParseGeoapifyResponse() throws IOException {
        String json = """
                {"features": [
                  {"properties": {"name": "Numi", "street": "Центральная улица", "housenumber": "27"}},
                  {"properties": {"street": "улица Баумана"}},
                  {"properties": {"name": "Пекарня", "street": "улица Баумана"}}
                ]}""";

        List<Place> places = new GeoapifyPlaces("test-key").parsePlaces(new ObjectMapper().readTree(json));

        Assertions.assertEquals(List.of("Numi, Центральная улица, 27", "Пекарня, улица Баумана"),
                places.stream().map(Place::format).toList());
    }

}
