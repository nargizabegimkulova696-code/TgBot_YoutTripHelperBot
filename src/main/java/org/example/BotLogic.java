package org.example;


import org.example.command.weather.WeatherProvider;
import org.example.handler.ButtonHandler;
import org.example.handler.StateService;
import org.example.handler.command.*;
import org.example.handler.states.TagHandler;
import org.example.handler.states.WaitingCityHandler;
import org.example.handler.states.WeatherDatesHandler;
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
        Map<String, ButtonHandler> handlers = new HashMap<>();

        ButtonHandler aboutCity = new AboutCityHandler(cityServer);
        ButtonHandler weather = new WeatherHandler();
        ButtonHandler food = new FoodHandler();
        ButtonHandler culture = new CultureHandler();
        ButtonHandler attractions = new AttractionsHandler();
        ButtonHandler back = new BackHandler();

        handlers.put("/start", new StartHandler());
        handlers.put("/help", new HelpHandler());

        handlers.put(AboutCityHandler.COMMAND, aboutCity);
        handlers.put(AboutCityHandler.CALLBACK, aboutCity);
        handlers.put(WeatherHandler.COMMAND, weather);
        handlers.put(WeatherHandler.CALLBACK, weather);
        handlers.put(FoodHandler.COMMAND, food);
        handlers.put(FoodHandler.CALLBACK, food);
        handlers.put(CultureHandler.COMMAND, culture);
        handlers.put(CultureHandler.CALLBACK, culture);
        handlers.put(AttractionsHandler.COMMAND, attractions);
        handlers.put(AttractionsHandler.CALLBACK, attractions);
        handlers.put(BackHandler.COMMAND, back);
        handlers.put(BackHandler.CALLBACK, back);


        this.buttonHandlers = handlers;

        this.stateService = new StateService(Map.of(
                DialogState.WAITING_FOR_CITY, new WaitingCityHandler(cityServer),
                DialogState.CAT_WEATHER, new WeatherDatesHandler(weatherProvider),
                DialogState.CAT_FOOD, tagHandler,
                DialogState.CAT_CULTURE, tagHandler,
                DialogState.CAT_ATTRACTIONS, tagHandler,
                DialogState.IN_TAG_FOOD, tagHandler,
                DialogState.IN_TAG_CULTURE, tagHandler,
                DialogState.IN_TAG_ATTRACTIONS, tagHandler
        ));
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