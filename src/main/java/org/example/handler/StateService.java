package org.example.handler;

import org.example.response.DialogResponse;
import org.example.response.KeyboardFactory;
import org.example.session.BotSession;
import org.example.session.DialogState;

import java.util.Map;

/**
 * Выбирает обработчик текстового сообщения по текущему состоянию диалога
 */
public class StateService {
    /**
     * Ответ, если в текущем состоянии бот не ждёт текста
     */
    private static final String WRONG_INTERACTION = "Чтобы воспользоваться ботом, нажми на нужную кнопку, " +
            "чтобы получить информацию или нажми back, чтобы вернуться к выбору города.";
    /**
     * Обработчики: ключ - состояние диалога, значение - обработчик этого состояния
     */
    private final Map<DialogState, StateHandler> handlers;

    /**
     * Клавиатура для сохранения кнопок при неправильном ответе
     */
    private final KeyboardFactory keyboard = new KeyboardFactory();

    public StateService(Map<DialogState, StateHandler> handlers) {
        this.handlers = handlers;
    }

    /**
     * Передаёт текст обработчику текущего состояния пользователя
     *
     * @param text    сообщение пользователя
     * @param session сессия пользователя
     * @return ответ бота
     */
    public DialogResponse handle(String text, BotSession session) {
        StateHandler handler = handlers.get(session.getState());
        if (handler == null) {
            return new DialogResponse(WRONG_INTERACTION, keyboard.build(session.getState()));
        }
        return handler.handle(text, session);
    }
}
