package org.example;

import org.example.AboutCity.AboutCity;
import org.example.BackCommand.BackCommand;
import org.example.DialogService.DialogResponse;
import org.example.DialogService.KeyboardFactory;
import org.example.SearchCityByName.CityValidator;
import org.example.Session.BotSession;
import org.example.Session.DialogState;
import org.example.Session.SessionStorage;
import org.example.Weather.OpenMeteoWeather;
import org.example.Weather.WeatherDialog;

/**
 * Логика работы бота, которая не зависит от API
 */
public class BotLogic {
    /**
     * Хранилище сессий пользователей
     */
    private final SessionStorage sessionStorage;

    /**
     * Константные сообщения для команд /start
     */
    private static final String START_MESSAGE = "Привет, я бот — помощник в путешествиях. В какой город вы хотели бы отправиться?";


    /**
     * Константные сообщения для команд /help
     */
    private static final String HELP_MESSAGE = """
            Я бот — помощник в путешествиях
            
            /start - начало работы
            /help - показать все доступные команды
            
            Доступные команды после выбора города:
            about_city - краткая сводка о городе
            weather - погода в городе
            back - вернуться к списку команд""";

    private static final String NOT_FOUNT_CITY = "Информации о городе не найдено";


    private static final String WRONG_INTERACTION = "Чтобы воспользоваться ботом, нажми на нужную кнопку, " +
            "чтобы получить информацию или нажми back, чтобы вернуться к выбору города.";

    private final CityValidator validator;

    /**
     * Создаёт логику бота, работающую с переданным хранилищем сессий
     */
    public BotLogic(SessionStorage sessionStorage, CityValidator validator) {
        this.sessionStorage = sessionStorage;
        this.validator = validator;
    }

    /**
     * Метод создан для создания сообщения в ответ пользователю
     * @param chatId идентификатор чата пользователя
     * @param text   текст сообщения, которое отправил пользователь
     * @return ответ бота
     */
    public DialogResponse makeMessage(String text, long chatId) {
        BotSession session = sessionStorage.getSession(chatId);
        BackCommand toBack = new BackCommand();
        WeatherDialog weatherDialog = new WeatherDialog(new OpenMeteoWeather());
        AboutCity aboutCity = new AboutCity(validator.getCatalog());

        DialogResponse response;
        KeyboardFactory keyboard = new KeyboardFactory();

        switch (text) {
            case "/start" -> {
                session.setState(DialogState.WAITING_FOR_CITY);
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
                response = new DialogResponse(aboutCity.describe(session.getCity()),
                        keyboard.build(session.getState()));
            }

            default -> {
                switch (session.getState()) {
                    case CAT_WEATHER -> {
                        response = weatherDialog.handleDates(text, session);
                    }
                    case WAITING_FOR_CITY -> {
                        if (!validator.contain(text).isEmpty()) {
                            session.setState(DialogState.CITY_SELECTED);
                            session.setCity(validator.contain(text));

                            response = new DialogResponse("Выберете категорию для города "
                                    + validator.contain(text) + ":",
                                    keyboard.build(session.getState()));
                        } else {
                            response = new DialogResponse(NOT_FOUNT_CITY);
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