package org.example;

import org.example.command.Back;
import org.example.command.weather.OpenMeteoWeather;
import org.example.command.weather.WeatherDialog;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.session.BotSession;
import org.example.session.DialogState;
import org.example.session.SessionStorage;

/**
 * Логика работы бота, которая не зависит от API
 */
public class BotLogic {
    /**
     * Хранилище сессий пользователей
     */
    private final SessionStorage sessionStorage = new SessionStorage();

    /**
     * Константные сообщения для команды /start
     */
    private static final String START_MESSAGE = "Привет, я бот — помощник в путешествиях. В какой город вы хотели бы отправиться?";


    /**
     * Константные сообщения для команды /help
     */
    private static final String HELP_MESSAGE = """
            Я бот — помощник в путешествиях
            
            /start - начало работы
            /help - показать все доступные команды
            
            Доступные команды после выбора города:
            about_city - краткая сводка о городе
            weather - погода в городе
            back - вернуться к списку команд""";

    /**
     * Константное сообщение, если город не найден
     */
    private static final String NOT_FOUND_CITY = "Информации о городе не найдено";

    /**
     * Константное сообщение, если сообщение от пользователя некорректно
     */
    private static final String WRONG_INTERACTION = "Чтобы воспользоваться ботом, нажми на нужную кнопку, " +
            "чтобы получить информацию или нажми back, чтобы вернуться к выбору города.";

    /**
     * Валидатор осуществляет поиск города в каталоге по его названию
     */
    private final CityServer server;

    /**
     * Конструктор логики бота - создание сессии пользователей и валидатора
     */
    public BotLogic(CityServer server) {
        this.server = server;
    }

    /**
     * Метод создан для создания сообщения в ответ пользователю
     * @param chatId идентификатор чата пользователя
     * @param text текст сообщения, которое отправил пользователь
     * @return ответ от бота
     */
    public DialogResponse makeMessage(String text, long chatId) {
        BotSession session = sessionStorage.getSession(chatId);

        Back toBack = new Back();
        WeatherDialog weatherDialog = new WeatherDialog(new OpenMeteoWeather());

        DialogResponse response;
        KeyboardFactory keyboard = new KeyboardFactory();

        switch (text) {
            case "/start" -> {
                session.setState(DialogState.WAITING_FOR_CITY);
                session.setCity(null);
                response = new DialogResponse(START_MESSAGE);
            }
            case "/help" -> {
                session.setState(DialogState.WAITING_FOR_CITY);
                response = new DialogResponse(HELP_MESSAGE);
            }

            case "cat:back" -> {
                response = toBack.back(session);
            }

            case "cat:weather" -> {
                response = weatherDialog.start(session);

            }

            case "cat:about_city" -> {
                session.setState(DialogState.CAT_ABOUT_CITY);
                response = new DialogResponse(server.findInfoForCity(session.getCity()),
                        keyboard.build(session.getState()));
            }

            default -> {
                switch (session.getState()) {
                    case CAT_WEATHER -> {
                        response = weatherDialog.handleDates(text, session);
                    }
                    case WAITING_FOR_CITY -> {
                        String city = server.findCityByName(text);

                        if (city != null) {
                            session.setState(DialogState.CITY_SELECTED);
                            session.setCity(city);

                            response = new DialogResponse("Выберете категорию для города "
                                    + city + ":",
                                    keyboard.build(session.getState()));
                        } else {
                            response = new DialogResponse(NOT_FOUND_CITY);
                        }
                    }
                    default -> {
                        response = new DialogResponse(WRONG_INTERACTION);
                    }
                }

            }
        }

        return response;
    }
}