package org.example.telegramBot;

import java.io.InputStream;
import java.util.Properties;

/**
 * Предоставляет доступ к токену и имени бота, создан для конфигурации бота
 */
public class ConfigTgBot {
    /**
     * Создано для хранения данных бота в виде пары: "ключ - значение"
     */
    private final Properties properties;

    /**
     * Конструктор класса ConfigTgBot, который читает данные с файла application.properties
     */
    public ConfigTgBot() {
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
     * возвращает имя тг-бота
     */
    public String getBotName(){
        return properties.getProperty("tgBotName");
    }

    /**
     * возвращает ключ API Geoapify
     */
    public String getGeoapifyKey() {
        return properties.getProperty("geoapifyKey");
    }

    /**
     * возвращает токен тг-бота
     */
    public String getBotToken(){
        return properties.getProperty("tgBotToken");
    }
}
