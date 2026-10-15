package org.example;


/**
 * Сессия одного пользователя: запоминает состояние и название города
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
     * Конструктор сессии по умолчанию, когда не задан город
     */
    public BotSession(){
        this.state = DialogState.WAITING_FOR_CITY;
        this.city = null;
    }

    /**
     * Возвращает текущее состояние диалога.
     *
     * @return текущее состояние диалога
     */
    public DialogState getState() {
        return state;
    }

    /**
     * Устанавливает состояние диалога.
     *
     * @param state новое состояние диалога
     */
    public void setState(DialogState state) {
        this.state = state;
    }

    /**
     * Возвращает город, выбранный пользователем.
     *
     * @return выбранный город или null, если город ещё не выбран
     */
    public String getCity() {
        return city;
    }

    /**
     * Устанавливает город, выбранный пользователем.
     *
     * @param city название выбранного города
     */
    public void setCity(String city) {
        this.city = city;
    }

}