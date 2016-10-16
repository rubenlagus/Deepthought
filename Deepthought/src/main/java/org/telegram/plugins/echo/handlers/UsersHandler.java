package org.telegram.plugins.echo.handlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.services.BotLogger;
import org.telegram.plugins.echo.database.DatabaseManagerImpl;
import org.telegram.plugins.echo.structure.User;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ruben Bermudez
 * @version 2.0
 * Handler for received users
 */
public class UsersHandler implements IUsersHandler {
    private static final String LOGTAG = "USERSHANDLER";
    private final ConcurrentHashMap<Integer, TLAbsUser> temporalUsers = new ConcurrentHashMap<>();
    private static final int MAXTEMPORALUSERS = 4000;
    private final DatabaseManagerImpl databaseManager;

    public UsersHandler(DatabaseManagerImpl databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Add a list of users to database
     * @param users List of users to add
     */
    public void onUsers(@NotNull List<TLAbsUser> users) {
        if ((this.temporalUsers.size() + users.size()) > MAXTEMPORALUSERS) {
            this.temporalUsers.clear();
        }
        users.stream().forEach(x -> this.temporalUsers.put(x.getId(), x));
        users.forEach(this::onUser);
    }

    /**
     * Add a user to database
     * @param absUser User to add
     */
    private void onUser(@NotNull TLAbsUser absUser) {
        User currentUser = null;
        User user = null;
        if (absUser instanceof TLUser) {
            final TLUser tlUser = (TLUser) absUser;
            if (tlUser.isMutualContact()) {
                currentUser = (User) databaseManager.getUserById(tlUser.getId());
                user = onUserContact(currentUser, tlUser);
            } else if (tlUser.isDeleted()) {
                currentUser = (User) databaseManager.getUserById(tlUser.getId());
                user = onUserDelete(currentUser, tlUser);
            } else if (tlUser.isContact()) {
                currentUser = (User) databaseManager.getUserById(tlUser.getId());
                user = onUserRequest(currentUser, tlUser);
            } else if (tlUser.isSelf() || !tlUser.isBot()) {
                currentUser = (User) databaseManager.getUserById(tlUser.getId());
                user = onUserForeign(currentUser, tlUser);
            } else {
                BotLogger.info(LOGTAG, "Bot received");
            }
        }
        if ((currentUser == null) && (user != null)) {
            databaseManager.addUser(user);
        } else if (user != null) {
            databaseManager.updateUser(user);
        }
    }

    /**
     * Create User from a delete user
     * @param currentUser Current use from database (null if not present)
     * @param userDeleted Delete user from Telegram Server
     * @return User information
     */
    private User onUserDelete(@Nullable User currentUser, @NotNull TLUser userDeleted) {
        final User user;
        if (currentUser == null) {
            user = new User(userDeleted.getId());
        } else {
            user = new User(currentUser);
        }
        user.setUserHash(0L);
        BotLogger.debug(LOGTAG, "userdeletedid: " + user.getUserId());
        return user;
    }

    /**
     * Create User from a contact user
     * @param currentUser Current use from database (null if not present)
     * @param userContact Contact user from Telegram Server
     * @return User information
     */
    private User onUserContact(@Nullable User currentUser, @NotNull TLUser userContact) {
        final User user;
        if (currentUser == null) {
            user = new User(userContact.getId());
        } else {
            user = new User(currentUser);
        }
        user.setUserHash(userContact.getAccessHash());
        BotLogger.debug(LOGTAG, "usercontactid: " + user.getUserId());
        return user;
    }

    /**
     * Create User from a request user
     * @param currentUser Current use from database (null if not present)
     * @param userRequest Request user from Telegram Server
     * @return User information
     */
    private User onUserRequest(@Nullable User currentUser, @NotNull TLUser userRequest) {
        final User user;
        if (currentUser == null) {
            user = new User(userRequest.getId());
        } else {
            user = new User(currentUser);
        }
        user.setUserHash(userRequest.getAccessHash());
        BotLogger.debug(LOGTAG, "userRequestId: " + user.getUserId());
        return user;
    }

    /**
     * Create User from a foreign user
     * @param currentUser Current use from database (null if not present)
     * @param userForeign Foreign user from Telegram Server
     * @return User information
     */
    private User onUserForeign(@Nullable User currentUser, @NotNull TLUser userForeign) {
        final User user;
        if (currentUser == null) {
            user = new User(userForeign.getId());
        } else {
            user = new User(currentUser);
        }
        user.setUserHash(userForeign.getAccessHash());
        BotLogger.debug(LOGTAG, "userforeignid: " + user.getUserId());
        return user;
    }

}
