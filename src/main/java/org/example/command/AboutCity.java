package org.example.command;

import org.apache.commons.lang3.StringUtils;
import org.example.catalog.CityCatalog;
import org.example.catalog.CityCatalog.Info;

import java.util.ArrayList;
import java.util.List;

/**
 * Inline-кнопка about_city - возвращает краткое описание города.
 * Данные берутся из каталога городов {@link CityCatalog}, а ответ собирается из блоков.
 */
public class AboutCity {
    public String format(Info info) {
        List<String> blocks = new ArrayList<>();

        // 1. Название и статус (название есть всегда)
        if (has(info.status())) {
            blocks.add(info.name() + " — " + info.status());
        } else {
            blocks.add(info.name());
        }

        // 2. Население и площадь
        List<String> popPlusArea = new ArrayList<>();
        if (has(info.population())) {
            popPlusArea.add("Население: " + info.population());
        }
        if (has(info.area())) {
            popPlusArea.add("Площадь: " + info.area());
        }
        addBlock(blocks, String.join("\n", popPlusArea));

        // 3. Описание
        addBlock(blocks, info.description());

        // 4. Климат
        if (has(info.climate())) {
            addBlock(blocks, "Климат: " + info.climate());
        }

        // 5. Интересные факты
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