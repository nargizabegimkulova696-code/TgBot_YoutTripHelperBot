package org.example.catalog.tags;

/**
 * Обработчик тегов для города
 */
public interface TagsCatalog {
    /**
     * Обработчик тега, возвращает строку, которая состоит из списка мест
     *
     * @param city - город, для которого ищем места
     * @param tag  - по которому определяются искомые места
     */
    String getPlaces(String city, String tag);
}
