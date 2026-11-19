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
    CAT_ABOUT_CITY,

    /**
     * Ожидание выбора тега для категории food
     */
    CAT_FOOD,

    /**
     * Ожидание выбора тега для категории culture
     */
    CAT_CULTURE,

    /**
     * Ожидание выбора тега для категории attractions
     */
    CAT_ATTRACTIONS,

    /**
     * Тег для категории food выбран
     */
    IN_TAG_FOOD,

    /**
     * Тег для категории culture выбран
     */
    IN_TAG_CULTURE,

    /**
     * Тег для категории attractions выбран
     */
    IN_TAG_ATTRACTIONS
}