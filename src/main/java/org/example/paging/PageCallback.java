package org.example.paging;

/**
 * Данные кнопки при просмотре мест: тег и номер страницы.
 */
public class PageCallback {
    /**
     * Начало callback кнопок more/back
     */
    private static final String PAGE_PREFIX = "page:";

    /**
     * Начало callback кнопок тегов
     */
    private static final String TAG_PREFIX = "tag:";

    /**
     * Тег, например "tag:cafe"
     */
    private final String tag;

    /**
     * Номер страницы, начиная с 0
     */
    private final int page;

    /**
     * Создание callback для страницы тега
     */
    public PageCallback(String tag, int page) {
        this.tag = tag;
        this.page = page;
    }

    /**
     * Разбор строки callback
     *
     * @param data строка вида "tag:cafe" или "page:tag:cafe:2"
     * @throws IllegalArgumentException если строка не является callback тега или страницы
     */
    public PageCallback(String data) {
        if (data.startsWith(TAG_PREFIX)) {
            this.tag = data;
            this.page = 0;
            return;
        }

        int separator = data.lastIndexOf(':');
        if (!data.startsWith(PAGE_PREFIX + TAG_PREFIX) || separator <= PAGE_PREFIX.length()) {
            throw new IllegalArgumentException("Не callback страницы: " + data);
        }

        this.tag = data.substring(PAGE_PREFIX.length(), separator);
        this.page = Integer.parseInt(data.substring(separator + 1));
    }


    public String getTag() {
        return tag;
    }

    /**
     * Номер страницы, начиная с 0
     */
    public int getPage() {
        return page;
    }

    /**
     * Строка callback для кнопки
     */
    public String toData() {
        return PAGE_PREFIX + tag + ":" + page;
    }
}