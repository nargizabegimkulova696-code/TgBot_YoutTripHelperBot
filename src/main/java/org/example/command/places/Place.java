package org.example.command.places;

/**
 * Одно место: название и адрес
 */
public class Place {
    /**
     * Название места
     */
    private final String name;

    /**
     * Адрес места, может быть пустым
     */
    private final String address;

    /**
     * Создание места с названием и адресом
     */
    public Place(String name, String address) {
        this.name = name;
        this.address = address;
    }

    /**
     * Название места
     */
    public String getName() {
        return name;
    }

    /**
     * Адрес места
     */
    public String getAddress() {
        return address;
    }

    /**
     * Название и адрес места, или название если нет адреса
     */
    public String format() {
        if (address.isBlank()) {
            return name;
        }
        return name + ", " + address;
    }
}