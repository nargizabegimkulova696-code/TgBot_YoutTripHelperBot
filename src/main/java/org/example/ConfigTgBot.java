package org.example;

import java.io.InputStream;
import java.util.Properties;

/**
 * Класс, который предоставляет доступ к токену и имени бота, создан для конфигурации бота
 */

public class ConfigTgBot {
    /**
     * Поле properties создано для хранения данных бота в виде пары: "ключ - значение"
     */
    private Properties properties;

    /**
     * Конструктор класса ConfigTgBot, который читает данные с файла application.properties
     */
    ConfigTgBot(){
        properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("tgBot.properties")){
            if(input == null){
                throw new RuntimeException("file tgBot.properties not found");
            }
            properties.load(input);
        } catch(Exception e){
            throw new RuntimeException("configuration file error", e);
        }
    }

    /**
     * Метод, который возвращает имя бота
     * @return String
     */
    public String getBotName(){
        return properties.getProperty("tgBotName");
    }

    /**
     * Метод, который возвращает токен бота
     * @return String
     */
    public String getBotToken(){
        return properties.getProperty("tgBotToken");
    }
}
