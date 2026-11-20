package org.example;


import org.example.command.weather.WeatherProvider;
import org.example.handler.ButtonHandler;
import org.example.handler.StateHandler;
import org.example.handler.StateService;
import org.example.handler.command.*;
import org.example.handler.command.categoryByPlaces.AttractionsHandler;
import org.example.handler.command.categoryByPlaces.CultureHandler;
import org.example.handler.command.categoryByPlaces.FoodHandler;
import org.example.handler.command.crudPlaces.AddPlaceHandler;
import org.example.handler.command.crudPlaces.DeletePlaceHandler;
import org.example.handler.command.crudPlaces.EditPlaceHandler;
import org.example.handler.states.TagHandler;
import org.example.handler.states.WaitingCityHandler;
import org.example.handler.states.WeatherDatesHandler;
import org.example.handler.states.crudPlaces.AddPlaceStateHandler;
import org.example.handler.states.crudPlaces.DeletePlaceStateHandler;
import org.example.handler.states.crudPlaces.EditPlaceStateHandler;
import org.example.response.DialogResponse;
import org.example.server.CityServer;
import org.example.server.TripServer;
import org.example.session.BotSession;
import org.example.session.DialogState;
import org.example.session.SessionStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * Логика работы бота, которая не зависит от API
 */
public class BotLogic {
    /**
     * Хранилище сессий пользователей
     */
    private final SessionStorage sessionStorage = new SessionStorage();

    /**
     * Обработчики команд и кнопок: ключ - текст команды или callbackData кнопки
     */
    private final Map<String, ButtonHandler> buttonHandlers;

    /**
     * Обработчик обычного текста в зависимости от состояния диалога
     */
    private final StateService stateService;

    /**
     * Конструктор логики бота, создаёт все обработчики один раз
     * @param cityServer доступ к каталогу городов
     * @param tripServer доступ к каталогам категорий: food, culture, attractions
     * @param weatherProvider источник прогноза погоды
     */
    public BotLogic(CityServer cityServer, TripServer tripServer, WeatherProvider weatherProvider) {
        TagHandler tagHandler = new TagHandler(tripServer);

        Map<String, ButtonHandler> buttonHandlers = new HashMap<>();
        Map<DialogState, StateHandler> stateHandlers = new HashMap<>();

        buttonHandlers.put("/start", new StartHandler());
        buttonHandlers.put("/help", new HelpHandler());
        buttonHandlers.put("/about_city", new AboutCityHandler(cityServer));
        buttonHandlers.put("/weather", new WeatherHandler());
        buttonHandlers.put("/food", new FoodHandler());
        buttonHandlers.put("/culture", new CultureHandler());
        buttonHandlers.put("/attractions", new AttractionsHandler());
        buttonHandlers.put("/back", new BackHandler());

        buttonHandlers.put("cat:weather", new WeatherHandler());
        buttonHandlers.put("cat:about_city", new AboutCityHandler(cityServer));
        buttonHandlers.put("cat:food", new FoodHandler());
        buttonHandlers.put("cat:culture", new CultureHandler());
        buttonHandlers.put("cat:attractions", new AttractionsHandler());
        buttonHandlers.put("cat:back", new BackHandler());

        buttonHandlers.put("com:add_place", new AddPlaceHandler());
        buttonHandlers.put("com:edit_place", new EditPlaceHandler());
        buttonHandlers.put("com:delete_place", new DeletePlaceHandler());

        this.buttonHandlers = buttonHandlers;

        stateHandlers.put(DialogState.WAITING_FOR_CITY, new WaitingCityHandler(cityServer));
        stateHandlers.put(DialogState.CATEGORY_WEATHER, new WeatherDatesHandler(weatherProvider));

        stateHandlers.put(DialogState.CATEGORY_FOOD, tagHandler);
        stateHandlers.put(DialogState.CATEGORY_CULTURE, tagHandler);
        stateHandlers.put(DialogState.CATEGORY_ATTRACTIONS, tagHandler);

        stateHandlers.put(DialogState.IN_TAG_FOOD, tagHandler);
        stateHandlers.put(DialogState.IN_TAG_CULTURE, tagHandler);
        stateHandlers.put(DialogState.IN_TAG_ATTRACTIONS, tagHandler);

        stateHandlers.put(DialogState.ADD_PLACE, new AddPlaceStateHandler(tripServer));

        stateHandlers.put(DialogState.EDIT_PLACE, new EditPlaceStateHandler(tripServer));
        stateHandlers.put(DialogState.CHANGE_NAME, new EditPlaceStateHandler(tripServer));
        stateHandlers.put(DialogState.CHANGE_ADDRESS, new EditPlaceStateHandler(tripServer));

        stateHandlers.put(DialogState.DELETE_PLACE, new DeletePlaceStateHandler(tripServer));

        this.stateService = new StateService(stateHandlers);
    }

    /**
     * Метод создан для создания сообщения в ответ пользователю:
     * сначала ищется обработчик команды/кнопки, иначе текст обрабатывается по состоянию диалога
     * @param text текст сообщения, которое отправил пользователь
     * @param chatId идентификатор чата пользователя
     * @return ответ от бота
     */
    public DialogResponse makeMessage(String text, long chatId) {
        BotSession session = sessionStorage.getSession(chatId);

        ButtonHandler button = buttonHandlers.get(text);
        if (button != null) {
            return button.execute(session);
        }
        return stateService.handle(text, session);
    }
}