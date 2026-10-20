package org.example.session;


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
     * Конструктор сессии изначальный, без заданного бота
     */
    public BotSession(){
        this.state = DialogState.WAITING_FOR_CITY;
        this.city = null;
    }

    /**
     * Возврат текущего состояния диалога.
     */
    public DialogState getState() {
        return state;
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
}