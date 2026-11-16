package org.example.utilities;

import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Класс для изменения состояния
 */
public class ChangeState {
    /**
     * Метод меняет состояние для корректной работы пейджинга
     * Переход от состояний ADD_PLACE, EDIT_PLACE, DELETE_PLACE к состоянию внутри тега
     *
     * @param session - сессия пользователя
     */
    public static void changeState(BotSession session) {
        switch (session.getCategory()) {
            case CAT_FOOD -> {
                session.setState(DialogState.IN_TAG_FOOD);
            }

            case CAT_CULTURE -> {
                session.setState(DialogState.IN_TAG_CULTURE);
            }

            default -> {
                session.setState(DialogState.IN_TAG_ATTRACTIONS);
            }
        }
    }
}
