package org.example.handler.command.crudPlaces;

import org.example.handler.ButtonHandler;
import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Обработчик кнопки edit:add_place
 */
public class EditPlaceHandler implements ButtonHandler {
    /**
     * Пользователь не может менять список мест, если не выбрал категорию и тег
     */
    private static final String WRONG_INTERACTION = "Сначала выберете категорию и тег, чтобы редактировать место)";

    /**
     * Ответное сообщение на команду изменить место
     */
    private static final String ANSWER = "Введите номер места, которое хотите изменить";

    /**
     * В состоянии EDIT_PLACE к текстовому сообщению добавляется кнопка "назад"
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    @Override
    public DialogResponse execute(BotSession session) {
        if (session.getCategory() == null || session.getTag() == null) {
            return new DialogResponse(WRONG_INTERACTION);
        }

        session.setState(DialogState.EDIT_PLACE);
        return new DialogResponse(ANSWER, keyboard.build(session.getState()));
    }
}
