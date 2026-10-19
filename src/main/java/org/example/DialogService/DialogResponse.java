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
    private final String answer;

    /**
     * Список категорий, доступный после выбора города
     */
    private final List<List<Button>> listOfCommands;

    /**
     * Создание текстового сообщения, которое получит пользователь в ответ
     */

    public DialogResponse(String answer) {
        this.answer = answer;
        listOfCommands = new ArrayList<>();
    }

    public DialogResponse(String answer, List<List<Button>> listOfCommands) {
        this.answer = answer;
        this.listOfCommands = listOfCommands;
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
     * Проверка на пустоту списка категорий
     */
    public boolean hasButtons() {
        return !listOfCommands.isEmpty();
    }
}
