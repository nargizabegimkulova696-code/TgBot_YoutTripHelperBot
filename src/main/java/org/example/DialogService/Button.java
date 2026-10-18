package org.example.DialogService;

/**
 * Хранит данные о inline кнопке
 */
public class Button {
    /**
     * Название категории, которое видит пользователь на кнопке
     */
    private final String nameOfCat;

    /**
     * Скрытое от пользователя название категории, по которому выполняется команда
     */
    private final String callBackData;

    /**
     * В конструкторе инициализируется кнопка
     */
    public Button(String catText, String callBackData) {
        nameOfCat = catText;
        this.callBackData = callBackData;
    }

    /**
     * Возврат названия категории
     */
    public String getCommandText() {
        return nameOfCat;
    }

    /**
     * Возврат названия скрытой от пользователя категории
     */
    public String getCallBackData() {
        return callBackData;
    }
}
