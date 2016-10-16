package org.telegram.plugins.echo;

import org.telegram.bot.ChatUpdatesBuilder;
import org.telegram.bot.handlers.UpdatesHandlerBase;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;
import org.telegram.bot.structure.BotConfig;
import org.telegram.plugins.echo.database.DatabaseManagerImpl;
import org.telegram.plugins.echo.handlers.MessageHandler;
import org.telegram.plugins.echo.handlers.TLMessageHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class ChatUpdatesBuilderImpl implements ChatUpdatesBuilder {
    private final Class<CustomUpdatesHandler> updatesHandlerBase;
    private IKernelComm kernelComm;
    private IUsersHandler usersHandler;
    private BotConfig botConfig;
    private IChatsHandler chatsHandler;
    private MessageHandler messageHandler;
    private TLMessageHandler tlMessageHandler;
    private IDifferenceParametersService differenceParametersService;
    private DatabaseManager databaseManager;

    public ChatUpdatesBuilderImpl(Class<CustomUpdatesHandler> updatesHandlerBase) {
        this.updatesHandlerBase = updatesHandlerBase;
    }

    @Override
    public void setKernelComm(IKernelComm kernelComm) {
        this.kernelComm = kernelComm;
    }

    @Override
    public void setDifferenceParametersService(IDifferenceParametersService differenceParametersService) {
        this.differenceParametersService = differenceParametersService;
    }

    @Override
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ChatUpdatesBuilderImpl setUsersHandler(IUsersHandler usersHandler) {
        this.usersHandler = usersHandler;
        return this;
    }

    public ChatUpdatesBuilderImpl setChatsHandler(IChatsHandler chatsHandler) {
        this.chatsHandler = chatsHandler;
        return this;
    }

    public ChatUpdatesBuilderImpl setBotConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
        return this;
    }

    public ChatUpdatesBuilderImpl setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        return this;
    }

    public ChatUpdatesBuilderImpl setDatabaseManager(DatabaseManagerImpl databaseManager) {
        this.databaseManager = databaseManager;
        return this;
    }

    public ChatUpdatesBuilderImpl setTlMessageHandler(TLMessageHandler tlMessageHandler) {
        this.tlMessageHandler = tlMessageHandler;
        return this;
    }

    @Override
    public UpdatesHandlerBase build() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (kernelComm == null) {
            throw new NullPointerException("Can't build the handler without a KernelComm");
        }
        if (differenceParametersService == null) {
            throw new NullPointerException("Can't build the handler without a differenceParamtersService");
        }

        messageHandler.setKernelComm(this.kernelComm);
        final Constructor<CustomUpdatesHandler> constructor = updatesHandlerBase.getConstructor(IKernelComm.class,
                IDifferenceParametersService.class, DatabaseManager.class);
        final CustomUpdatesHandler updatesHandler =
                constructor.newInstance(kernelComm, differenceParametersService, getDatabaseManager());
        updatesHandler.setConfig(botConfig);
        updatesHandler.setHandlers(messageHandler, usersHandler, chatsHandler, tlMessageHandler);
        return updatesHandler;
    }
}
