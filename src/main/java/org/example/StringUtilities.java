package org.example;

/**
 * Утилиты для строк
 */
public class StringUtilities {

    /**
     * Метод приводит название города к "нормализованному" виду;
     * Это нужно, чтобы город находился независимо от того, как ввел его пользователь.
     * Преобразования:
     * <ul>
     *     <li>Все буквы переводятся в нижний регистр;</li>
     *     <li>Буква "ё" заменяется на "е";</li>
     *     <li>Дефисы заменяются на пробелы;</li>
     *     <li>Несколько пробелов подряд заменяются одним, пробелы по краям удаляются.</li>
     * </ul>
     *
     * @param str - на вход строка с названием города
     * @return string result - "нормализованное" название города
     */
    public static String normalizeCityName(String str) {

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
