package com.attendance;

import java.sql.*;

/** All database work related to attendance. */
public class AttendanceDAO {

    /** Marks attendance for today. Returns true if newly marked, false if already marked. */
    public boolean markPresent(int studentId) {
        String sql = "INSERT IGNORE INTO attendance (student_id, att_date, att_time) "
                   + "VALUES (?, CURDATE(), CURTIME())";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;   // 0 rows = already marked today
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    /** Prints the attendance report for a date (format yyyy-MM-dd). */
    public void printReport(String date) {
        String sql = "SELECT s.roll_no, s.name, a.att_time FROM attendance a "
                   + "JOIN students s ON s.id = a.student_id "
                   + "WHERE a.att_date = ? ORDER BY a.att_time";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            System.out.println("\n--- Attendance on " + date + " ---");
            System.out.printf("%-12s %-25s %s%n", "Roll No", "Name", "Time");
            int count = 0;
            while (rs.next()) {
                System.out.printf("%-12s %-25s %s%n",
                        rs.getString("roll_no"), rs.getString("name"), rs.getString("att_time"));
                count++;
            }
            System.out.println("Total present: " + count);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}