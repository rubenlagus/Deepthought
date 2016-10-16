package org.telegram;

import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.echo.BotConfigImpl;
import org.telegram.plugins.echo.ChatUpdatesBuilderImpl;
import org.telegram.plugins.echo.CustomUpdatesHandler;
import org.telegram.plugins.echo.database.DatabaseManagerImpl;
import org.telegram.plugins.echo.handlers.ChatsHandler;
import org.telegram.plugins.echo.handlers.MessageHandler;
import org.telegram.plugins.echo.handlers.TLMessageHandler;
import org.telegram.plugins.echo.handlers.UsersHandler;

import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class Deepthought {
    private static final int APIKEY = 0; // your api key
    private static final String APIHASH = "<your-api-hash"; // your api hash
    private static final String PHONENUMBER = "<your-phone-number>"; // Your phone number

    public static void main(String[] args) {
        Logger.getGlobal().addHandler(new ConsoleHandler());
        Logger.getGlobal().setLevel(Level.ALL);


        final DatabaseManagerImpl databaseManager = new DatabaseManagerImpl();
        final BotConfig botConfig = new BotConfigImpl(PHONENUMBER);

        final IUsersHandler usersHandler = new UsersHandler(databaseManager);
        final IChatsHandler chatsHandler = new ChatsHandler(databaseManager);
        final MessageHandler messageHandler = new MessageHandler();
        final TLMessageHandler tlMessageHandler = new TLMessageHandler(messageHandler, databaseManager);

        final ChatUpdatesBuilderImpl builder = new ChatUpdatesBuilderImpl(CustomUpdatesHandler.class);
        builder.setBotConfig(botConfig)
                .setDatabaseManager(databaseManager)
                .setUsersHandler(usersHandler)
                .setChatsHandler(chatsHandler)
                .setMessageHandler(messageHandler)
                .setTlMessageHandler(tlMessageHandler);

        try {
            final TelegramBot kernel = new TelegramBot(botConfig, builder, APIKEY, APIHASH);
            LoginStatus status = kernel.init();
            if (status == LoginStatus.CODESENT) {
                Scanner in = new Scanner(System.in);
                boolean success = kernel.getKernelAuth().setAuthCode(in.nextLine().trim());
                if (success) {
                    status = LoginStatus.ALREADYLOGGED;
                }
            }
            if (status == LoginStatus.ALREADYLOGGED) {
                kernel.startBot();
            } else {
                throw new Exception("Failed to log in: " + status);
            }
        } catch (Exception e) {
            BotLogger.severe("MAIN", e);
        }
    }
}
