package org.example.command;

import org.apache.commons.lang3.StringUtils;

import org.example.catalog.CityCatalog.Info;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс отвечает за форматирование текста, который видит пользователь при выборе категории - about_city
 */
public class AboutCity {
    /**
     * Метод форматирует блоки информации, которые хранятся в передаваемом параметре,
     * в текст сообщения для пользователя
     */
    public String format(Info info) {
        List<String> blocks = new ArrayList<>();

        if (has(info.status())) {
            blocks.add(info.name() + " — " + info.status());
        } else {
            blocks.add(info.name());
        }

        List<String> popPlusArea = new ArrayList<>();
        if (has(info.population())) {
            popPlusArea.add("Население: " + info.population());
        }
        if (has(info.area())) {
            popPlusArea.add("Площадь: " + info.area());
        }
        addBlock(blocks, String.join("\n", popPlusArea));

        addBlock(blocks, info.description());

        if (has(info.climate())) {
            addBlock(blocks, "Климат: " + info.climate());
        }

        if (info.facts() != null && !info.facts().isEmpty()) {
            blocks.add("Интересные факты:\n— " + String.join("\n— ", info.facts()));
        }

        return String.join("\n\n", blocks);
    }

    /**
     * Добавляет в список блоков, содержащих информацию о городе, непустой блок
     */
    private void addBlock(List<String> blocks, String text) {
        if (has(text)) {
            blocks.add(text);
        }
    }

    /**
     * Перед добавлением в список блоков проверка, что строка в блоке непустая
     */
    private boolean has(String text) {
        return StringUtils.isNotBlank(text);
    }
}