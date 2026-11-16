package org.example.command.places;

/**
 * Одно место: название и адрес
 */
public class Place {
    /**
     * Название места
     */
    private String name;

    /**
     * Адрес места, может быть пустым
     */
    private String address;

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
     * Смена названия места
     *
     * @param name - новое название
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Адрес места
     */
    public String getAddress() {
        return address;
    }

    /**
     * Смена адреса места
     *
     * @param address - новый адрес
     */
    protected void setAddress(String address) {
        this.address = address; }

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