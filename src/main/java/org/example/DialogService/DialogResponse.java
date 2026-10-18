package org.example.DialogService;

import java.util.ArrayList;
import java.util.List;

/**
 * Хранит информацию об ответном сообщении пользователю
 */
public class DialogResponse {
    /**
     * Текстовое сообщение
     */
    private String answer;

    /**
     * Список категорий, доступный после выбора города
     */
    private final List<List<Button>> listOfCommands = new ArrayList<>();

    /**
     * Создание текстового сообщения, которое получит пользователь в ответ
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Возврат ответного текстового сообщения
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Возврат списка категорий
     */
    public List<List<Button>> getListOfCommands() {
        return listOfCommands;
    }

    /**
     * Очистка списка категорий
     */
    public void clearListOfCommands() {
        listOfCommands.clear();
    }

    /**
     * Проверка на пустоту списка категорий
     */
    public boolean hasButtons() {
        return !listOfCommands.isEmpty();
    }

    /**
     * Изменение списка категорий в зависимости от состояния пользователя
     */
    public void changeKeyboardState(DialogState dialog) {
        switch (dialog) {
            case WAITING_FOR_CITY -> {
                clearListOfCommands();
            }
            case CAT_ABOUT_CITY -> {
            }
            case CAT_WEATHER -> {
            }
            case CITY_SELECTED -> {
                clearListOfCommands();
                ;

                List<Button> command1 = new ArrayList<>();
                command1.add(new Button("about city", "cat:about_city"));

                List<Button> command2 = new ArrayList<>();
                command2.add(new Button("weather", "cat:weather"));

                this.listOfCommands.add(command1);
                this.listOfCommands.add(command2);
            }
        }
    }
}
