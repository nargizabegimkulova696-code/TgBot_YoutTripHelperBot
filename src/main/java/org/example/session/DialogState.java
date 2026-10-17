package org.example.session;

/**
 * Состояние диалога бота с пользователем.
 */
public enum DialogState {
    /**
     * Бот ждет, пока пользователь выберет город
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