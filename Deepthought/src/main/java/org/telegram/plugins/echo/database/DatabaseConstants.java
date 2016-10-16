package org.telegram.plugins.echo.database;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
class DatabaseConstants {
    static final String controllerDB = "com.mysql.cj.jdbc.Driver";
    static final String userDB = "<your-own-db-user>";
    private static final String databaseName = "<your-own-db-name>";
    static final String password = "<your-own-db-password>";
    static final String linkDB = "jdbc:mysql://localhost:3306/" + databaseName + "?useUnicode=true&characterEncoding=UTF-8";
}
