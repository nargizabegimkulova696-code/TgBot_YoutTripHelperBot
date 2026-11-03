package org.example;

/**
 * Утилиты для строк
 */
public class StringUtilities {

    /**
     * Статический метод для нормализации названия города, для его корректного поиска.
     * Преподаватель разрешил использование static для этого метода
     *
     * @param str - на вход строка с названием города
     * @return string result - нормализованное название города
     */
    public static String normal(String str) {

        if (org.apache.commons.lang3.StringUtils.isBlank(str)) {
            return "";
        }

        String result = str
                .toLowerCase()
                .replace('ё', 'е')
                .replace('-', ' ')
                .replaceAll("\\s+", " ")
                .trim();

        return result;
    }
}
