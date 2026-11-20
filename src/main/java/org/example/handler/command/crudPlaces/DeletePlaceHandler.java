package org.example.handler.command.crudPlaces;

import org.example.handler.ButtonHandler;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Обработчик кнопки com:delete_place
 */
public class DeletePlaceHandler implements ButtonHandler {
    /**
     * Пользователь не может менять список мест, если не выбрал категорию и тег
     */
    private static final String WRONG_INTERACTION = "Сначала выберете категорию и тег, чтобы удалить место)";

    /**
     * Ответное сообщение на команду удалить место
     */
    private static final String ANSWER = "Введите номер места, которое хотите удалить";

    /**
     * В состоянии DELETE_PLACE к текстовому сообщению добавляется кнопка "назад"
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    @Override
    public DialogResponse execute(BotSession session) {
        if (session.getCategory() == null || session.getTag() == null) {
            return new DialogResponse(WRONG_INTERACTION);
        }
        ;

        session.setState(DialogState.DELETE_PLACE);
        return new DialogResponse(ANSWER, keyboard.build(session.getState()));
    }
}
