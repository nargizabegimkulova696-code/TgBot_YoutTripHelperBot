package org.example.handler.states.crudPlaces;

import org.example.handler.StateHandler;
import org.example.handler.states.TagHandler;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.server.ServiceState;
import org.example.server.TripServer;
import org.example.session.BotSession;
import org.example.storage.UserPlace;

/**
 * Обработчик действий, которые происходят в состоянии ADD_PLACE
 */
public class AddPlaceStateHandler implements StateHandler {
    /**
     * Сервер, для запросов к бд
     */
    private final TripServer server;

    /**
     * Обработчик тегов создает финальное сообщение
     * с добавленным или не добавленным местом.
     * Переводит пользователя обратно в состояние тега.
     */
    private final TagHandler tagHandler;

    /**
     * Меняет состояние сессии на состояние категории, в которой находится пользователь,
     * чтобы далее управление вернулось к TagHandler
     */
    private final ServiceState serviceState;

    /**
     * Сообщение, если название и адрес места введены не по формату или на прочие действия,
     * которые не обрабатываются в этом состоянии
     */
    private static final String WRONG_INPUT = "Некорректный формат ввода";

    /**
     * Вопрос перед добавлением
     */
    private static final String ADD_PLACE = "Добавляю в список место: ";

    /**
     * Исключение от бд
     */
    private static final String SQL_ERROR = "Не удалось добавить место, повторите попытку позже)";

    /**
     * Ошибка при запросе к API мест
     */
    private static final String ERROR = "Не удалось вывести список мест, повторите попытку позже)";

    /**
     * Сообщение при успешном добавлении места
     */
    private static final String SAVE_ANSWER = "Место успешно добавлено в список\n\n";

    /**
     * Сообщение, если пользователь решил не добавлять место в список
     */
    private static final String CANCEL_ANSWER = "Место не добавлено в список\n\n";

    /**
     * Формат ввода содержит 2 значения: название и адрес
     */
    private static final int DATA = 2;

    /**
     * Заглушка для id в бд
     */
    private static final long ID = 0L;

    /**
     * Клавиатура кнопок, для ответных сообщений
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    /**
     * Конструктор класса
     *
     * @param server - TripServer, для работы с бд
     */
    public AddPlaceStateHandler(TripServer server) {
        this.server = server;
        this.tagHandler = new TagHandler(server);
        this.serviceState = new ServiceState();
    }

    @Override
    public DialogResponse handle(String text, BotSession session) {
        switch (text) {
            case "com:save" -> {
                UserPlace place = session.getPlace();

                if (!server.addPlace(session.getChatId(), session.getCity(), place.getCategory(),
                        place.getTag(), place.getName(), place.getAddress())) {
                    return new DialogResponse(SQL_ERROR, keyboard.build(session.getState()));
                }

                serviceState.changeOnCategoryState(session);

                DialogResponse response = tagHandler.handle(session.getTag(), session);

                return new DialogResponse(SAVE_ANSWER + response.getAnswer(),
                        response.getListOfCommands());
            }

            case "com:cancel" -> {
                serviceState.changeOnCategoryState(session);

                DialogResponse response = tagHandler.handle(session.getTag(), session);

                return new DialogResponse(CANCEL_ANSWER + response.getAnswer(),
                        response.getListOfCommands());
            }

            default -> {
                String[] newPlace = text.split(",");

                if (newPlace.length != DATA) {
                    return new DialogResponse(WRONG_INPUT, keyboard.build(session.getState()));
                }

                String name = newPlace[0].trim();
                String address = newPlace[1].trim();
                String category = session.getCategory();

                UserPlace place = new UserPlace(ID, category,
                        session.getTag(), name, address);

                session.setPlace(place);

                return new DialogResponse(ADD_PLACE + place.format(), keyboard.buildChoose());
            }
        }
    }
}
