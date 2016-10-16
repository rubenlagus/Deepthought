package org.telegram.plugins.echo.structure;

import org.telegram.bot.structure.IUser;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class User implements IUser {
    private final int userId; ///< ID of the user (provided by Telegram server)
    private Long userHash; ///< Hash of the user (provide by Telegram server)

    public User(int uid) {
        this.userId = uid;
    }

    public User(User copy) {
        this.userId = copy.getUserId();
        this.userHash = copy.getUserHash();
    }

    @Override
    public int getUserId() {
        return this.userId;
    }

    @Override
    public Long getUserHash() {
        return userHash;
    }

    public void setUserHash(Long userHash) {
        this.userHash = userHash;
    }

    @Override
    public String toString() {
        return "" + this.userId;
    }
}
