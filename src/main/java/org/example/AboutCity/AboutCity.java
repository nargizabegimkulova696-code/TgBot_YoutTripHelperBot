package org.example.AboutCity;

import org.example.Catalog.CityCatalog;
import org.example.Catalog.CityCatalog.Info;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда /about_city, которая дает краткую сводку про город.
 * Данные берутся из каталога городов {@link CityCatalog}, а ответ собирается из блоков.
 */
public class AboutCity {
    /**
     * Каталог, из которого берутся справки о городах
     */
    private final CityCatalog catalog;

    /**
     * Ответ, если города нет в каталоге
     */
    public static final String NOT_FOUND = "Информации о городе не найдено";

    /**
     * Создаёт сводку на основе готового каталога
     */
    public AboutCity(CityCatalog catalog) {
        this.catalog = catalog;
    }

    /**
     * Возвращает текст сводки о городе
     */
    public String describe(String city) {
        Info info = catalog.findInfo(city);
        if (info == null) {
            return NOT_FOUND;
        }
        return format(info);
    }

    /**
     * Склеивает блоки, пропуская пустые
     */
    private String format(Info info) {
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
        // 5. Интересные факты
        if (info.facts() != null && !info.facts().isEmpty()) {
            blocks.add("Интересные факты:\n— " + String.join("\n— ", info.facts()));
        }

        return String.join("\n\n", blocks);
    }

    /**
     * Добавляет блок, если в нём есть текст
     */
    private void addBlock(List<String> blocks, String text) {
        if (has(text)) {
            blocks.add(text);
        }
    }

    /**
     * Проверяет, что в строке есть текст
     */
    private boolean has(String text) {
        return text != null && !text.isBlank();
    }
}