package org.example.handler.command;

import org.example.handler.ButtonHandler;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Обработчик кнопки com:add_place
 */
public class AddPlaceHandler implements ButtonHandler {
    /**
     * Ответное сообщение на команду добавить место
     */
    private static final String ANSWER = "Введите название места, " +
            "которое хотите добавить, в формате: Название, Адрес";

    /**
     * Пользователь не может менять список мест, если не выбрал категорию и тег
     */
    private static final String WRONG_INTERACTION = "Сначала выберете категорию и тег, чтобы добавить место)";

    /**
     * В состоянии ADD_PLACE к текстовому сообщению добавляется кнопка "назад"
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    @Override
    public DialogResponse execute(BotSession session) {
        if (session.getCategory() == null || session.getTag() == null) {
            return new DialogResponse(WRONG_INTERACTION);
        }

        session.setState(DialogState.ADD_PLACE);
        return new DialogResponse(ANSWER, keyboard.build(session.getState()));
    }
}
