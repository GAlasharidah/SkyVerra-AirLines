package com.mycompany.airlinebookingsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() {
        Connection connection = null;
        try {
            String url = "jdbc:mysql://localhost:3306/skyvera";
            String username = "root";
            String password = "Amnesaifndara1*";

            connection = DriverManager.getConnection(url, username, password);
            System.out.println(" Connection Successful!");
        } catch (SQLException e) {
            System.out.println(" Connection Failed: " + e.getMessage());
        }
        return connection;
    }

    public static void main(String[] args) {
        getConnection();
    }
}
