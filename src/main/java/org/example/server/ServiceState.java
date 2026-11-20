package org.example.server;

import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Класс предоставляет метод для смены состояния сессии
 */
public class ServiceState {
    /**
     * Меняет состояние сессии на то, которое написано у него в категории
     * т.е. если category = food, то state = CATEGORY_FOOD
     *
     * @param session - сессия одного пользователя
     */
    public void changeOnCategoryState(BotSession session) {
        switch (session.getCategory()) {
            case "food" -> {
                session.setState(DialogState.CATEGORY_FOOD);
            }
            case "culture" -> {
                session.setState(DialogState.CATEGORY_CULTURE);
            }
            default -> {
                session.setState(DialogState.CATEGORY_ATTRACTIONS);
            }
        }
    }
}
