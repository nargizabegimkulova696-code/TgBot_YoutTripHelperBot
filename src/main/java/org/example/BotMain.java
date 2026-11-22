package org.example;

import org.example.cityLoader.CityLoader;
import org.example.cityLoader.CsvCityLoader;
import org.example.command.city.CityProvider;
import org.example.command.city.WikimediaRestCity;
import org.example.command.places.GeoapifyPlaces;
import org.example.command.weather.OpenMeteoWeather;
import org.example.platform.DiscordBot;
import org.example.platform.TgBot;
import org.example.server.CityServer;
import org.example.server.TripServer;
import org.example.storage.UserPlaceStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

/**
 * Точка входа в программу для запуска ботов в Telegram и Discord
 */
public class BotMain {
    /**
     * Вывод ошибок, которые могли возникнуть при инициализации бота
     */
    private static final Logger LOG = LoggerFactory.getLogger("BotMain");

    /**
     * Точка запуска программы
     */
    public static void main(String[] args) throws TelegramApiException{
        ConfigBot config = new ConfigBot();

        //получаем из настроек имя и токены
        String BotName = config.getBotName();
        String tgBotToken = config.getBotToken();
        String discordBotToken = config.getDiscordToken();
        String geoApiKey = config.getGeoapifyKey();

        CityLoader cityLoader = new CsvCityLoader();

        try {
            CityProvider catalog = cityLoader.load();

            CityServer cityServer = new CityServer(new WikimediaRestCity(), catalog);
            UserPlaceStorage userPlaceStorage = new UserPlaceStorage("jdbc:sqlite:places.db");
            TripServer tripServer = new TripServer(new GeoapifyPlaces(geoApiKey), userPlaceStorage);

            if (BotName == null || BotName.isBlank() || tgBotToken == null || tgBotToken.isBlank()) {
                LOG.warn("Бот Telegram пропущен: не указано имя или токен");
            } else {
                // создаём логику с отдельными сессиями для Telegram
                BotLogic tgBotLogic = new BotLogic(cityServer, tripServer, new OpenMeteoWeather());
                TgBot helperTripBot = new TgBot(BotName, tgBotToken, tgBotLogic);

                // подключаем бота к Telegram
                helperTripBot.start();
            }

            if (discordBotToken == null || discordBotToken.isBlank()) {
                LOG.warn("Бот Discord пропущен: не указан токен");
            } else {
                // создаём логику с отдельными сессиями для Discord
                BotLogic discordBotLogic = new BotLogic(cityServer, tripServer, new OpenMeteoWeather());
                DiscordBot discordBot = new DiscordBot(discordBotToken, discordBotLogic);

                // подключаем бота к Discord
                discordBot.start();
            }
        } catch (IOException e) {
            LOG.error("Не удалось прочитать файлы и в связи с этим запустить бота", e);
        }
    }
}