package com.attendance;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/** All database work related to students. */
public class StudentDAO {

    /** Adds a student and returns the new student id (-1 if failed). */
    public int addStudent(String name, String rollNo) {
        String sql = "INSERT INTO students (name, roll_no) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, rollNo);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Could not add student: " + e.getMessage());
        }
        return -1;
    }

    /** Returns a map of studentId -> name (used to show names on the camera). */
    public Map<Integer, String> getAllNames() {
        Map<Integer, String> map = new HashMap<>();
        String sql = "SELECT id, name FROM students";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getInt("id"), rs.getString("name"));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return map;
    }
}