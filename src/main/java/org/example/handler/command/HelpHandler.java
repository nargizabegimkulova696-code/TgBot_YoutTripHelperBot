package org.example.handler.command;

import org.example.handler.ButtonHandler;
import org.example.response.DialogResponse;
import org.example.session.BotSession;
import org.example.session.DialogState;

/**
 * Команда /help: показывает список команд и возвращает к выбору города
 */
public class HelpHandler implements ButtonHandler {
    /**
     * Сообщение со списком команд
     */
    private static final String HELP_MESSAGE = """
            Я бот — помощник в путешествиях
            
            /start - начало работы
            /help - показать все доступные команды
            
            Доступные команды после выбора города:
            about_city - краткая сводка о городе
            weather - погода в городе
            back - вернуться к списку команд""";

    @Override
    public DialogResponse execute(BotSession session) {
        session.setState(DialogState.WAITING_FOR_CITY);
        return new DialogResponse(HELP_MESSAGE);
    }
}