package org.telegram.plugins.echo.handlers;

import org.jetbrains.annotations.NotNull;
import org.telegram.api.engine.RpcException;
import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.IUser;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class MessageHandler {
    private static final String LOGTAG = "MESSAGEHANDLER";
    private IKernelComm kernelComm;

    public MessageHandler() {
    }

    public void setKernelComm(IKernelComm kernelComm) {
        this.kernelComm = kernelComm;
    }

    /**
     * Handler for the request of a contact
     *
     * @param user    User to be answered
     * @param message TLMessage received
     */
    public void handleMessage(@NotNull IUser user, @NotNull TLMessage message) {
        try {
            handleMessageInternal(user, message.getMessage());
        } catch (RpcException e) {
            BotLogger.severe(LOGTAG, e);
        }
    }

    /**
     * Handler for the requests of a contact
     *
     * @param user    User to be answered
     * @param message Message received
     */
    public void handleMessage(@NotNull IUser user, @NotNull TLUpdateShortMessage message) {
        try {
            handleMessageInternal(user, message.getMessage());
        } catch (RpcException e) {
            BotLogger.severe(LOGTAG, e);
        }
    }

    /**
     * Handle a message from an user
     * @param user User that sent the message
     * @param message Message received
     */
    private void handleMessageInternal(@NotNull IUser user, String message) throws RpcException {
        kernelComm.sendMessage(user, message);
        kernelComm.performMarkAsRead(user, 0);
    }
}
