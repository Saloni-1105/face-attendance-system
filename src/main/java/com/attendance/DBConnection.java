package com.attendance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Gives other classes a connection to MySQL. */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/face_attendance";
    private static final String USER = "attendance_user";
    private static final String PASSWORD = "S@loni123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
