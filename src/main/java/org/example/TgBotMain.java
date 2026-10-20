package org.example;

import org.example.catalog.CityCatalog;

import org.example.catalog.loaderOfCatalog.CatalogLoader;
import org.example.catalog.loaderOfCatalog.CsvCatalogLoader;

import org.example.telegramBot.ConfigTgBot;
import org.example.telegramBot.TgBot;

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
    private static final Logger LOG = LoggerFactory.getLogger("TgBotMain");

    /**
     * Точка запуска программы
     */
    public static void main(String[] args) throws TelegramApiException{
        ConfigTgBot config = new ConfigTgBot();

        //получаем из настроек имя и токен для конструктора TgBot
        String tgBotName = config.getBotName();
        String tgBotToken = config.getBotToken();

        CatalogLoader loader = new CsvCatalogLoader();
        try {
            CityCatalog catalog = loader.load();
            CityServer serverForCities = new CityServer(catalog);

            BotLogic botLogic = new BotLogic(serverForCities);

            TgBot helperTripBot = new TgBot(tgBotName, tgBotToken, botLogic);

            //связали бота с тг
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(helperTripBot);

            LOG.info("Бот зарегистрирован");
        } catch (IOException e) {
            LOG.error("Не удалось прочитать файл </city_info.csv> и в связи с этим запустить бота", e);
        }
    }
}