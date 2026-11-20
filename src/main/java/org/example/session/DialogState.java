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
    CATEGORY_WEATHER,

    /**
     * Ожидание выбора следующей категории
     */
    CATEGORY_ABOUT_CITY,

    /**
     * Ожидание выбора тега для категории food
     */
    CATEGORY_FOOD,

    /**
     * Ожидание выбора тега для категории culture
     */
    CATEGORY_CULTURE,

    /**
     * Ожидание выбора тега для категории attractions
     */
    CATEGORY_ATTRACTIONS,

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
    IN_TAG_ATTRACTIONS,

    /**
     * Состояние добавления места
     */
    ADD_PLACE,

    /**
     * Состояние редактирования места
     */
    EDIT_PLACE,

    /**
     * Состояние смены имени
     */
    CHANGE_NAME,

    /**
     * Состояние смены адреса
     */
    CHANGE_ADDRESS,

    /**
     * Состояние удаления места
     */
    DELETE_PLACE
}