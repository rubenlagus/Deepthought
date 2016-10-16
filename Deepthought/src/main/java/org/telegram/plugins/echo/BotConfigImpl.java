package org.telegram.plugins.echo;

import org.telegram.bot.structure.BotConfig;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class BotConfigImpl extends BotConfig {
    private String phoneNumber;

    public BotConfigImpl(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        setAuthfile(phoneNumber + ".auth");
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String getBotToken() {
        return null;
    }

    @Override
    public boolean isBot() {
        return false;
    }
}
