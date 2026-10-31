package org.example.paging;

import java.util.List;

/**
 * Одна страница списка с нужной логикой: элементы страницы, её номер и общее число страниц.
 *
 * @param <T> тип элементов списка
 */
public class Page<T> {
    /**
     * Элементы этой страницы
     */
    private final List<T> items;

    /**
     * Номер страницы, начиная с 0
     */
    private final int number;

    /**
     * Общее число страниц, 0 - если список пустой
     */
    private final int totalPages;

    /**
     * Позиция первого элемента страницы во всём списке, начиная с 0
     */
    private final int firstIndex;

    /**
     * Выбор страницы из всего списка
     * Если номер страницы выходит за границы, берётся ближайшая существующая страница
     *
     * @param all    весь список
     * @param number номер нужной страницы, начиная с 0
     * @param size   сколько элементов на странице
     */
    public Page(List<T> all, int number, int size) {
        this.totalPages = (all.size() + size - 1) / size;
        this.number = Math.max(0, Math.min(number, totalPages - 1));
        this.firstIndex = this.number * size;
        this.items = List.copyOf(all.subList(firstIndex, Math.min(firstIndex + size, all.size())));
    }

    /**
     * Элементы этой страницы
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * Номер страницы, начиная с 0
     */
    public int getNumber() {
        return number;
    }

    /**
     * Общее число страниц
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Позиция первого элемента страницы во всём списке, начиная с 0
     */
    public int getFirstIndex() {
        return firstIndex;
    }

    /**
     * Есть ли следующая страница
     */
    public boolean hasNext() {
        return number + 1 < totalPages;
    }

    /**
     * Есть ли предыдущая страница
     */
    public boolean hasPrevious() {
        return number > 0;
    }

    /**
     * Пустой ли список
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}