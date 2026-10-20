package org.example.session;

/**
 * Состояние бота в диалоге с пользователем
 */
public enum DialogState {
    /**
     * Ожидание выбора города
     */
    WAITING_FOR_CITY,

    /**
     * Город выбран, ожидание выбора категории
     */
    CITY_SELECTED,

    /**
     * Ожидание ввода дат для прогноза погоды
     */
    CAT_WEATHER,

    /**
     * Ожидание выбора следующей категории
     */
    CAT_ABOUT_CITY

}