package org.example.storage;

import org.example.command.places.Place;

/**
 * Место, добавленное пользователем. Это обычное место (название и адрес),
 * у которого дополнительно есть идентификатор в базе, категория и тег
 */
public class UserPlace extends Place {
    /**
     * Идентификатор места в базе данных
     */
    private final long id;

    /**
     * Категория места: "food", "culture" или "attractions"
     */
    private final String category;

    /**
     * Тег, в котором находится место, например "tag:cafe"
     */
    private final String tag;

    /**
     * Создание места, добавленного пользователем
     */
    public UserPlace(long id, String category, String tag, String name, String address) {
        super(name, address);
        this.id = id;
        this.category = category;
        this.tag = tag;
    }

    /**
     * Идентификатор места в базе данных
     */
    public long getId() {
        return id;
    }

    /**
     * Категория места
     */
    public String getCategory() {
        return category;
    }

    /**
     * Тег, в котором находится место
     */
    public String getTag() {
        return tag;
    }

    /**
     * Эмодзи по категории
     */
    public String getMark() {
        return switch (category) {
            case "food" -> "🥨";
            case "culture" -> "🕌";
            default -> "🎢";
        };
    }

    /**
     * Название и адрес места со смайликом категории в конце
     */
    @Override
    public String format() {
        return super.format() + " " + getMark();
    }
}