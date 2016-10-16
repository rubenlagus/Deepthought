package org.telegram.plugins.echo.structure;

import org.telegram.bot.structure.Chat;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class ChatImpl implements Chat {
    private int id;
    private Long accessHash;
    private boolean isChannel;

    public ChatImpl(int id) {
        this.id = id;
    }

    public ChatImpl() {
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Long getAccessHash() {
        return accessHash;
    }

    @Override
    public boolean isChannel() {
        return isChannel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccessHash(Long accessHash) {
        this.accessHash = accessHash;
    }

    public void setChannel(boolean channel) {
        isChannel = channel;
    }
}
