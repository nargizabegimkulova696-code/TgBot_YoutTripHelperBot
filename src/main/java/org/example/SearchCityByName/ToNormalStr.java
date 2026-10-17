package org.example.SearchCityByName;

/**
 * Нормализация строк, для корректной проверки
 */
public class ToNormalStr {
    /**
     * Метод нормализирует строку
     */
    public String normal(String str) {

        if (str == null || str.isBlank()) {
            return "";
        }

        String result = str
                .toLowerCase()
                .replace('ё', 'е')
                .replace('-', ' ')
                .replaceAll("\\s+", " ")
                .trim();

        return Character.toUpperCase(result.charAt(0)) + result.substring(1);
    }
}
