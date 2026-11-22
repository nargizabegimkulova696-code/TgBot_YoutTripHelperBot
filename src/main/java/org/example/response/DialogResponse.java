package org.example.response;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Хранит ответное сообщение пользователю
 */
public class DialogResponse {
    /**
     * Ссылка на фото города
     */
    private final String photoUri;

    /**
     * Текстовое сообщение
     */
    private final String answer;

    /**
     * Список категорий, доступный после выбора города
     */
    private final List<List<Button>> listOfCommands;

    /**
     * Создание текстового сообщения, которое получит пользователь в ответ
     */
    public DialogResponse(String answer) {
        this.answer = answer;

        listOfCommands = new ArrayList<>();
        photoUri = "";
    }

    /**
     * Создание ответного сообщения: текст сообщения + список категорий, которое получит пользователь в ответ
     */
    public DialogResponse(String answer, List<List<Button>> listOfCommands) {
        this.answer = answer;
        this.listOfCommands = listOfCommands;

        photoUri = "";
    }

    /**
     * Ответное сообщению пользователю в категории "о городе",
     * если для города найдена фотография
     */
    public DialogResponse(String photoUri, String answer, List<List<Button>> listOfCommands) {
        this.photoUri = photoUri;
        this.answer = answer;
        this.listOfCommands = listOfCommands;
    }

    /**
     * Возврат ответного текстового сообщения
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Возврат списка категорий
     */
    public List<List<Button>> getListOfCommands() {
        return listOfCommands;
    }

    /**
     * Возврат ссылки на фото города
     */
    public String getPhotoUri() {
        return photoUri;
    }

    /**
     * Проверка на пустоту списка категорий
     */
    public boolean hasButtons() {
        return !listOfCommands.isEmpty();
    }

    /**
     * Проверка на пустоту ссылки фото для города
     */
    public boolean hasPhotoUri() {
        return StringUtils.isNotBlank(photoUri);
    }
}
