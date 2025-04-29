package com.example.bugs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.bugs.model.SignUp;
import com.example.bugs.util.DatabaseUtil;

public class UserDAO {
    
    public boolean createUser(SignUp user) {
        String sql = "INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword()); // In a real app, hash the password!
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking email: " + e.getMessage());
        }
        return false;
    }
    
    public boolean validateLogin(String email, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND password = ?";
        
        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password); // Note: In production, compare hashed passwords
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error validating login: " + e.getMessage());
        }
        return false;
    }
}