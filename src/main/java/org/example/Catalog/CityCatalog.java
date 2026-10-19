package org.example.Catalog;

/**
 * Интерфейс, создан для работы с каталогами данных
 */
public interface CityCatalog {
    /**
     * Возврат города по его названию
     */
    public String findCity(String nameCity);
}
