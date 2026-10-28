package org.example;


import org.example.command.weather.WeatherProvider;
import org.example.handler.ButtonHandler;
import org.example.handler.StateService;
import org.example.handler.command.*;
import org.example.handler.states.WaitingCityHandler;
import org.example.handler.states.WeatherDatesHandler;
import org.example.response.DialogResponse;
import org.example.session.BotSession;
import org.example.session.DialogState;
import org.example.session.SessionStorage;

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
     * @param server доступ к каталогу городов
     * @param weatherProvider источник прогноза погоды
     */
    public BotLogic(CityServer server, WeatherProvider weatherProvider) {
        this.buttonHandlers = Map.of(
                "/start", new StartHandler(),
                "/help", new HelpHandler(),
                "/about_city", new AboutCityHandler(server),
                "/weather", new WeatherHandler(),
                "/back", new BackHandler(),
                "cat:back", new BackHandler(),
                "cat:weather", new WeatherHandler(),
                "cat:about_city", new AboutCityHandler(server)
        );

        this.stateService = new StateService(Map.of(
                DialogState.WAITING_FOR_CITY, new WaitingCityHandler(server),
                DialogState.CAT_WEATHER, new WeatherDatesHandler(weatherProvider)
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