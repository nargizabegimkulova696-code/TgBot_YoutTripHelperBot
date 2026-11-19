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
 * Тестирование пейджинга, а именно текстового сообщения
 * и кнопок "ещё" и "назад", которые идут в дополнении к сообщению.
 */
public class PagingTest {
    /**
     * Фейковый источник вместо API
     */
    private final PlacesProvider provider = new FakeProvider();

    /**
     * Обработчик тегов
     */
    private final TagHandler handler = new TagHandler(new TripService(provider));

    /**
     * Сессия пользователя
     */
    private final BotSession session = new BotSession();

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
     * Тестирование первой страницы: доступно 5 мест, кнопки "далее" и "назад"
     */
    @Test
    void testFirstPage() {
        DialogResponse response = handler.handle("tag:cafe", session);

        Assertions.assertEquals("""
                Места, где можно вкусно покушать:
                
                - Кафе 1, ул. Ленина, 1
                
                - Кафе 2, ул. Ленина, 2
                
                - Кафе 3
                
                - Кафетерий 4, ул. Тургенева, 4
                
                - Кафе 5, ул. Ленина, 5
                
                Страница 1 из 2""", response.getAnswer());
        Assertions.assertEquals(List.of("page:tag:cafe:1", "cat:back"), callbacks(response));
        Assertions.assertEquals(DialogState.IN_TAG_FOOD, session.getState());
    }

    /**
     * Тестирование средней страницы: доступны 5 мест,
     * кнопка далее ведет на следующую страницу,
     * кнопка назад на предыдущую
     */
    @Test
    void TestMiddlePage() {
        //пользователь выбрал тег пекарня
        handler.handle("tag:bakeries", session);

        //пользователь нажал кнопку далее
        // ("page:tag:bakeries:1" - callback кнопки далее)
        DialogResponse response = handler.handle("page:tag:bakeries:1", session);

        Assertions.assertEquals("""
                Места, где можно вкусно покушать:
                
                - Тита и Йойо, ул. «Цар Шишман» 2
                
                - Галета, пекарня и кафе, ул. «Екзарх Йосиф» 36
                
                - Уан Бейкери, ул. «Черковна» 68
                
                - Пура Вита, бельгийская пекарня, ул. «Цар Освободител» 27
                
                - Кейк Лаб, ул. «Карнеградска» 14
                
                Страница 2 из 3""", response.getAnswer());
        Assertions.assertEquals(List.of("page:tag:bakeries:2", "page:tag:bakeries:0"), callbacks(response));
        Assertions.assertEquals(DialogState.IN_TAG_FOOD, session.getState());
    }

    /**
     * Тестирование последней страницы: доступно 2 места,
     * кнопки "далее" нет, "назад" ведёт на первую страницу
     */
    @Test
    void testLastPage() {
        //пользователь выбрал тег кафе
        handler.handle("tag:cafe", session);

        //пользователь нажал кнопку далее
        //"page:tag:cafe:1" - callback кнопки,
        // с помощью которой осуществляется переход на следующую страницу
        DialogResponse response = handler.handle("page:tag:cafe:1", session);

        Assertions.assertEquals("""
                Места, где можно вкусно покушать:
                
                - Кафе 6, ул. Московская, 6
                
                - Кафе 7, ул. 8 марта, 8
                
                Страница 2 из 2""", response.getAnswer());
        Assertions.assertEquals(1, response.getListOfCommands().size());
        Assertions.assertEquals(List.of("page:tag:cafe:0"), callbacks(response));
    }

}
