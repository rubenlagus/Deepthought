package org.telegram.plugins.echo.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;
import org.telegram.plugins.echo.structure.ChatImpl;
import org.telegram.plugins.echo.structure.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class DatabaseManagerImpl implements DatabaseManager {
    private static final String LOGTAG = "DATABASEMANAGER";
    private static volatile ConnectionDB connetion;

    /**
     * Private constructor (due to Singleton)
     */
    public DatabaseManagerImpl() {
        connetion = new ConnectionDB();
        final int currentVersion = connetion.checkVersion();
        BotLogger.info(LOGTAG, "Current db version: " + currentVersion);
        if (currentVersion < CreationStrings.version) {
            recreateTable(currentVersion);
        }
    }

    /**
     * Recreates the DB
     */
    private void recreateTable(int currentVersion) {
        try {
            connetion.initTransaction();
            if (currentVersion == 0) {
                createNewTables();
            }
            connetion.commitTransaction();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    private int createNewTables() throws SQLException {
        connetion.executeQuery(CreationStrings.createVersionTable);
        connetion.executeQuery(CreationStrings.createUsersTable);
        connetion.executeQuery(CreationStrings.insertCurrentVersion);
        connetion.executeQuery(CreationStrings.createChatTable);
        connetion.executeQuery(CreationStrings.createDifferencesDataTable);
        return CreationStrings.version;
    }

    /**
     * Gets an user by id
     *
     * @param userId ID of the user
     * @return User requested or null if it doesn't exists
     * @see User
     */
    @Override
    public @Nullable IUser getUserById(int userId) {
        User user = null;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("SELECT * FROM Users WHERE userId= ?");
            preparedStatement.setInt(1, userId);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                user = new User(userId);
                user.setUserHash(result.getLong("userHash"));
            }
            result.close();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return user;
    }

    /**
     * Adds an user to the database
     *
     * @param user User to be added
     * @return true if it was added, false otherwise
     * @see User
     */
    public boolean addUser(@NotNull User user) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("INSERT INTO Users (userId, userHash) " +
                    "VALUES (?,?)");
            preparedStatement.setInt(1, user.getUserId());
            if ((user.getUserHash() == null) || (user.getUserHash() == 0L)) {
                preparedStatement.setNull(2, Types.NUMERIC);
            } else {
                preparedStatement.setLong(2, user.getUserHash());
            }
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return updatedRows > 0;
    }

    public boolean updateUser(@NotNull User user) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("UPDATE Users SET userHash=? " +
                    "WHERE userId=?");
            if ((user.getUserHash() == null) || (user.getUserHash() == 0L)) {
                preparedStatement.setNull(1, Types.NUMERIC);
            } else {
                preparedStatement.setLong(1, user.getUserHash());
            }
            preparedStatement.setInt(2, user.getUserId());
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return updatedRows > 0;
    }

    @Override
    public @Nullable Chat getChatById(int chatId) {
        ChatImpl channel = null;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("SELECT * FROM Chat WHERE id= ?");
            preparedStatement.setInt(1, chatId);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                channel = new ChatImpl(chatId);
                channel.setAccessHash(result.getLong("accessHash"));
                channel.setChannel(result.getBoolean("isChannel"));
            }
            result.close();
        } catch (SQLException e) {
            BotLogger.severe(LOGTAG, e);
        }

        return channel;
    }

    /**
     * Adds a chat to the database
     *
     * @param chat User to be added
     * @return true if it was added, false otherwise
     * @see User
     */
    public boolean addChat(@NotNull ChatImpl chat) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("INSERT INTO Chat (id, accessHash, isChannel) " +
                    "VALUES (?,?,?)");
            preparedStatement.setInt(1, chat.getId());
            if (chat.getAccessHash() == null) {
                preparedStatement.setNull(2, Types.BIGINT);
            } else {
                preparedStatement.setLong(2, chat.getAccessHash());
            }
            preparedStatement.setBoolean(3, chat.isChannel());
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return updatedRows > 0;
    }

    public boolean updateChat(ChatImpl chat) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("UPDATE Chat SET accessHash=?, isChannel=? " +
                    "WHERE id=?");
            preparedStatement.setLong(1, chat.getAccessHash());
            preparedStatement.setBoolean(2, chat.isChannel());
            preparedStatement.setInt(3, chat.getId());
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return updatedRows > 0;
    }

    @Override
    public @NotNull HashMap<Integer, int[]> getDifferencesData() {
        final HashMap<Integer, int[]> differencesDatas = new HashMap<>();
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("SELECT * FROM DifferencesData");
            final ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                final int[] differencesData = new int[3];
                differencesData[0] = result.getInt("pts");
                differencesData[1] = result.getInt("date");
                differencesData[2] = result.getInt("seq");
                differencesDatas.put(result.getInt("botId"), differencesData);
            }
            result.close();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG,e);
        }
        return differencesDatas;
    }

    @Override
    public boolean updateDifferencesData(int botId, int pts, int date, int seq) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("REPLACE INTO DifferencesData (botId, pts, date, seq) VALUES (?, ?, ?, ?);");
            preparedStatement.setInt(1, botId);
            preparedStatement.setInt(2, pts);
            preparedStatement.setInt(3, date);
            preparedStatement.setInt(4, seq);
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG,e);
        }
        return updatedRows > 0;
    }

    @Override
    protected void finalize() throws Throwable {
        connetion.closeConexion();
        super.finalize();
    }

}
