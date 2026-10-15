package org.example;

/**
 * Состояние диалога бота с одним пользователем.
 */
public enum DialogState {
    /**
     * Ожидание ввода названия города
     */
    WAITING_FOR_CITY,
    /**
     * Город выбран, и сейчас меню
     */
    CITY_SELECTED,
    /**
     * Бот ждёт, что пользователь введёт период дат для погоды
     */
    WAITING_FOR_DATES
}