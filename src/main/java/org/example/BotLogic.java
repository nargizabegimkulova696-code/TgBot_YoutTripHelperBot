package org.example;

/**
 * Логика работы бота, которая не зависит от API
 */
public class BotLogic {
    /**
     * Метод создан для создания сообщения в ответ пользователю
     * @param text текст сообщения, которое отправил пользователь
     * @return String
     */
    public String makeMessage(String text){

        String answer;

        switch (text){
            case "/start" -> answer = "Привет, я Эхо-Бот, отправь мне свое сообщение и я повторю его за тобой!";
            case "/help" -> answer = """
                Привет, я Эхо-Бот
                /start - начало работы
                /help - показать все доступные команды""";


            default -> answer = "Вы ввели " + "«" + text + "»";
        }

        return answer;
    }
}