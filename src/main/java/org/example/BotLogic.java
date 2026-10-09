package org.example;

/**
 * Логика работы бота, которая не зависит от API
 */
public class BotLogic {
    /**
     * Константные сообщения для команд /start
     */
    private static final String START_MESSAGE = "Привет, я Эхо-Бот, " + "отправь мне свое сообщение и я повторю его за тобой!";

    /**
     * Константные сообщения для команд /help
     */
    private static final String HELP_MESSAGE = """
            Привет, я Эхо-Бот
            /start - начало работы
            /help - показать все доступные команды""";
    /**
     * Метод создан для создания сообщения в ответ пользователю
     */
    public String makeMessage(String text){

        String answer;

        switch (text){
            case "/start" -> answer = START_MESSAGE;
            case "/help" -> answer = HELP_MESSAGE;
            default -> answer = "Вы ввели «" + text + "»";
        }

        return answer;
    }
}