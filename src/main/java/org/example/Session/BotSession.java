package org.example.Session;


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
     * Конструктор сессии изначальный, без заданного бота
     */
    public BotSession(){
        this.state = DialogState.WAITING_FOR_CITY;
        this.city = null;
    }

    /**
     * Возвращает текущее состояние диалога.
     */
    public DialogState getState() {
        return state;
    }

    /**
     * Устанавливает состояние диалога.
     */
    public void setState(DialogState state) {
        this.state = state;
    }

    /**
     * Возвращает город, выбранный пользователем.
     */
    public String getCity() {
        return city;
    }

    /**
     * Устанавливает город, выбранный пользователем.
     */
    public void setCity(String city) {
        this.city = city;
    }
}