package org.example.session;


import org.example.storage.UserPlace;

/**
 * Сессия одного пользователя: хранит текущее состояние и название города
 */
public class BotSession {
    /**
     * Текущее состояние диалога с пользователем
     */
    private DialogState state;

    /**
     * Город, который выбрал пользователь, или null, если город ещё не выбран
     */
    private String city;

    /**
     * Категория, в которой находится пользователь
     * Задействуется для категорий: еда, культура, развлечения
     */
    private String category;

    /**
     * Тег, в котором находится пользователь
     */
    private String tag;

    /**
     * Идентификатор чата пользователя
     */
    private final long chatId;

    /**
     * Место пользователя, которое он добавляет/редактирует/удаляет
     */
    private UserPlace place;

    /**
     * Создание сессии пользователя с заданным идентификатором чата
     */
    public BotSession(long chatId) {
        this.chatId = chatId;
        this.state = DialogState.WAITING_FOR_CITY;
        this.city = null;
        this.category = null;
        this.tag = null;
        this.place = null;
    }

    /**
     * Возврат текущего состояния диалога.
     */
    public DialogState getState() {
        return state;
    }

    /**
     * Возврат идентификатора чата пользователя.
     */
    public long getChatId() {
        return chatId;
    }

    /**
     * Смена состояния диалога.
     */
    public void setState(DialogState state) {
        this.state = state;
    }

    /**
     * Возврат города, выбранный пользователем.
     */
    public String getCity() {
        return city;
    }

    /**
     * Смена города, выбранного пользователем.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Меняем значение категории
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Возврат текущей категории
     */
    public String getCategory() {
        return category;
    }

    /**
     * Меняем значение тега
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Возврат текущего тега
     */
    public String getTag() {
        return tag;
    }

    /**
     * Меняем место на другое, на то, которое пользователь хочет
     * добавить, изменить или удалить
     */
    public void setPlace(UserPlace place) {
        this.place = place;
    }

    /**
     * Возврат места
     */
    public UserPlace getPlace() {
        return place;
    }
}