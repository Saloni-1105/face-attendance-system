package com.attendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Database queries used by the teacher dashboard. */
public class DashboardDAO {

    /** Total number of registered students. */
    public int countStudents() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return 0;
    }

    /** Students present today: each row = {roll no, name, time}. */
    public List<String[]> getPresentToday() {
        String sql = "SELECT s.roll_no, s.name, a.att_time "
                   + "FROM attendance a JOIN students s ON s.id = a.student_id "
                   + "WHERE a.att_date = CURDATE() ORDER BY a.att_time";
        return runQuery(sql, 3);
    }

    /** Students absent today: each row = {roll no, name}. */
    public List<String[]> getAbsentToday() {
        String sql = "SELECT roll_no, name FROM students "
                   + "WHERE id NOT IN (SELECT student_id FROM attendance WHERE att_date = CURDATE()) "
                   + "ORDER BY roll_no";
        return runQuery(sql, 2);
    }

    /** Runs a query and returns every row as a String array. */
    private List<String[]> runQuery(String sql, int columns) {
        List<String[]> rows = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String[] row = new String[columns];
                for (int i = 0; i < columns; i++) {
                    row[i] = rs.getString(i + 1);
                }
                rows.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return rows;
    }
}