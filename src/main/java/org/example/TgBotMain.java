package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TgBotMain{
    public static void main(String[] args) throws TelegramApiException{
        ConfigTgBot config = new ConfigTgBot();

        //создаем имя и токен для конструктора TgBot
        String tgBotName = config.getBotName();
        String tgBotToken = config.getBotToken();

        TgBot echoBot = new TgBot(tgBotName, tgBotToken);

        //связали бота с тг
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(echoBot);
    }
}