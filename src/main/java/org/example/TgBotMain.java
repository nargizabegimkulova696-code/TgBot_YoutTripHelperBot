package org.example;

import org.example.Catalog.CityCatalog;

import org.example.LoaderOfCatalog.CatalogLoader;
import org.example.LoaderOfCatalog.CsvCatalogLoader;

import org.example.SearchCityByName.CityValidator;

import org.example.Session.SessionStorage;

import org.example.TelegramBot.ConfigTgBot;
import org.example.TelegramBot.TgBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;

/**
 * Точка входа в программу для запуска тг бота
 */
public class TgBotMain{
    /**
     * Вывод ошибок, которые могли возникнуть при инициализации бота
     */
    private static final Logger log = LoggerFactory.getLogger("TgBotMain");

    public static void main(String[] args) throws TelegramApiException{
        ConfigTgBot config = new ConfigTgBot();

        //получаем из настроек имя и токен для конструктора TgBot
        String tgBotName = config.getBotName();
        String tgBotToken = config.getBotToken();

        CatalogLoader loader = new CsvCatalogLoader();
        try {
            CityCatalog catalog = loader.load();
            CityValidator validator = new CityValidator(catalog);

            SessionStorage sessionStorage = new SessionStorage();

            BotLogic botLogic = new BotLogic(sessionStorage, validator);

            TgBot helperTripBot = new TgBot(tgBotName, tgBotToken, botLogic);

            //связали бота с тг
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(helperTripBot);

            log.info("Бот зарегистрирован");
        } catch (IOException e) {
            log.error("Не удалось прочитать файл </cities.csv> и в связи с этим запустить бота", e);
        }
    }
}