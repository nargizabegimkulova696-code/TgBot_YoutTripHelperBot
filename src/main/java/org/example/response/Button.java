package org.example.response;

import java.util.Objects;

/**
 * Хранит данные об inline кнопке
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

    @Override
    public boolean equals(Object o) {
        return o instanceof Button other
                && nameOfCat.equals(other.nameOfCat)
                && callBackData.equals(other.callBackData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameOfCat, callBackData);
    }

    @Override
    public String toString() {
        return "Button[" + nameOfCat + " -> " + callBackData + "]";
    }
}
