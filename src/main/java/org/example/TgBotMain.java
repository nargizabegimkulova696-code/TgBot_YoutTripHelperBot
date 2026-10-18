package org.example;

import org.example.Catalog.CityCatalog;
import org.example.LoaderOfCatalog.CatalogLoader;
import org.example.LoaderOfCatalog.CsvCatalogLoader;
import org.example.TelegramBot.ConfigTgBot;
import org.example.TelegramBot.TgBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;

/**
 * Точка входа в программу для запуска тг бота
 */
public class TgBotMain{
    public static void main(String[] args) throws TelegramApiException{
        ConfigTgBot config = new ConfigTgBot();

        //получаем из настроек имя и токен для конструктора TgBot
        String tgBotName = config.getBotName();
        String tgBotToken = config.getBotToken();

        CatalogLoader loader = new CsvCatalogLoader();
        try {
            CityCatalog catalog = loader.load();
            TgBot echoBot = new TgBot(tgBotName, tgBotToken, catalog);

            //связали бота с тг
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(echoBot);
        } catch (IOException e) {
            System.err.println("Не удалось прочитать файл </cities.csv>" + e);
        }
    }
}