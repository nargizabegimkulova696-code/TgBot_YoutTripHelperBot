package org.example;

import org.example.command.places.PlacesProvider;
import org.example.handler.states.TagHandler;
import org.example.response.Button;
import org.example.response.DialogResponse;
import org.example.service.TripService;
import org.example.session.BotSession;
import org.example.session.DialogState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Тестирование обработчика тегов, а именно текстовых сообщений + кнопки,
 * которые получает пользователь при разных сценариях работы с тегами
 */
public class TagHandlerTest {

    /**
     * Фейковый источник мест, вместо API
     */
    private final PlacesProvider placesProvider = new FakeProvider();

    /**
     * Через сервис идет взаимодействие с источником мест
     */
    private final TripService tripService = new TripService(placesProvider);

    /**
     * Обработчик тегов, который тестируется
     */
    private final TagHandler tagHandler = new TagHandler(tripService);

    /**
     * Сессия одного пользователя
     */
    private final BotSession userSession = new BotSession();

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
     * Тестирование сообщения, которое получает пользователь, находясь в теге, если вместо выбора кнопок
     * пишет текстовые сообщения
     */
    @Test
    void testIncorrectMessageInTag() {
        userSession.setCity("Москва");
        userSession.setState(DialogState.IN_TAG_FOOD);

        DialogResponse response = tagHandler.handle("hello", userSession);

        Assertions.assertEquals("Выберите, что вас интересует, с помощью кнопок.", response.getAnswer());
        Assertions.assertEquals(List.of("cat:back"), callbacks(response));
        Assertions.assertEquals(DialogState.IN_TAG_FOOD, userSession.getState());
    }

    /**
     * Тестирование сообщения, которое получает пользователь,
     * находясь в категории мест: еда, развлечения или культура,
     * если вместо выбора кнопок
     * пишет текстовые сообщения
     */
    @Test
    void testIncorrectMessageInCategory() {
        userSession.setCity("Екатеринбург");
        userSession.setState(DialogState.CAT_FOOD);

        DialogResponse response = tagHandler.handle("something", userSession);

        Assertions.assertEquals("Выберите, что вас интересует, с помощью кнопок.", response.getAnswer());
        Assertions.assertEquals(List.of("tag:restaurants",
                "tag:cafe",
                "tag:bakeries",
                "tag:pizzerias",
                "tag:fastfood",
                "cat:back"), callbacks(response));
        Assertions.assertEquals(DialogState.CAT_FOOD, userSession.getState());
    }

    /**
     * Тестирование сообщения, если API не нашел по запрашиваемому тегу места
     */
    @Test
    void TestNotFoundPlaces() {
        userSession.setCity("Ижевск");
        userSession.setState(DialogState.CAT_CULTURE);

        DialogResponse response = tagHandler.handle("tag:monuments", userSession);

        Assertions.assertEquals("Места для тега не найдены(", response.getAnswer());
        Assertions.assertEquals(List.of("cat:back"), callbacks(response));
        Assertions.assertEquals(DialogState.IN_TAG_CULTURE, userSession.getState());
    }

    /**
     * Тестирование сообщения, если API нашел по запрашиваемому тегу места
     */
    @Test
    void TestFoundPlaces() {
        userSession.setCity("Казань");
        userSession.setState(DialogState.CAT_FOOD);

        DialogResponse response = tagHandler.handle("tag:bakeries", userSession);

        Assertions.assertEquals("""
                Места, где можно вкусно покушать:
                
                - ДжоВан, голландская пекарня, ул. «Ангел Кънчев» 37
                
                - Предимно Пекарна, ул. «Княз Александър Дондуков» 49
                
                - СинаМомс
                
                - Фурна, «Стефан Стамболов» 3
                
                - хляб, ул. «Екзарх Йосиф» 69
                
                Страница 1 из 3""", response.getAnswer());
        Assertions.assertEquals(List.of("page:tag:bakeries:1", "cat:back"), callbacks(response));
        Assertions.assertEquals(DialogState.IN_TAG_FOOD, userSession.getState());
    }

    /**
     * Тестирование сообщения, если выброшено исключение при запросе к API
     */
    @Test
    void TestIOExceptionFromAPI() {
        userSession.setCity("Нижний Новгород");
        userSession.setState(DialogState.CAT_ATTRACTIONS);

        DialogResponse response = tagHandler.handle("tag:movie", userSession);

        Assertions.assertEquals("Не удалось загрузить места. Попробуйте позже.",
                response.getAnswer());
        Assertions.assertEquals(List.of("cat:back"), callbacks(response));
        Assertions.assertEquals(DialogState.IN_TAG_ATTRACTIONS, userSession.getState());
    }
}
