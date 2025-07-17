package com.group_P1.wellness.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.security.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;
import com.zaxxer.hikari.*;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    
    private HikariDataSource dataSource;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5353/PRG381_wellness");
            config.setUsername("postgres");
            config.setPassword("Ven06246");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new ServletException("Database initialization failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get and trim all form parameters
        String firstName = request.getParameter("firstName") != null ? request.getParameter("firstName").trim() : "";
        String lastName = request.getParameter("lastName") != null ? request.getParameter("lastName").trim() : "";
        String username = request.getParameter("username") != null ? request.getParameter("username").trim() : "";
        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";
        String phone = request.getParameter("phone") != null ? request.getParameter("phone").replaceAll("[^0-9]", "") : "";
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Store parameters in request attributes to repopulate form
        request.setAttribute("firstName", firstName);
        request.setAttribute("lastName", lastName);
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);

        // Validate inputs
        Map<String, String> errors = new HashMap<>();

        if (firstName.isEmpty()) {
            errors.put("firstNameError", "First name is required");
        } else if (firstName.length() < 2) {
            errors.put("firstNameError", "First name must be at least 2 characters");
        }

        if (lastName.isEmpty()) {
            errors.put("lastNameError", "Last name is required");
        } else if (lastName.length() < 2) {
            errors.put("lastNameError", "Last name must be at least 2 characters");
        }

        if (username.isEmpty()) {
            errors.put("usernameError", "Username is required");
        } else if (username.length() < 4) {
            errors.put("usernameError", "Username must be at least 4 characters");
        } else if (!username.matches("^[a-zA-Z0-9_]+$")) {
            errors.put("usernameError", "Username can only contain letters, numbers and underscores");
        }

        if (email.isEmpty()) {
            errors.put("emailError", "Email is required");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errors.put("emailError", "Please enter a valid email address");
        }

        if (phone.isEmpty()) {
            errors.put("phoneError", "Phone number is required");
        } else if (phone.length() != 10) {
            errors.put("phoneError", "Please enter a valid 10-digit phone number");
        }

        if (password == null || password.isEmpty()) {
            errors.put("passwordError", "Password is required");
        } else if (password.length() < 8) {
            errors.put("passwordError", "Password must be at least 8 characters");
        } else if (!password.matches(".*[A-Z].*")) {
            errors.put("passwordError", "Password must contain at least one uppercase letter");
        } else if (!password.matches(".*\\d.*")) {
            errors.put("passwordError", "Password must contain at least one number");
        } else if (!password.matches(".*[!@#$%^&*].*")) {
            errors.put("passwordError", "Password must contain at least one special character");
        }

        if (!confirmPassword.equals(password)) {
            errors.put("confirmPasswordError", "Passwords do not match");
        }

        // If there are validation errors, forward back to the form
        if (!errors.isEmpty()) {
            // Add all error messages to request attributes
            for (Map.Entry<String, String> error : errors.entrySet()) {
                request.setAttribute(error.getKey(), error.getValue());
            }
            
            // Forward back to the registration page
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // If validation passes, proceed with registration
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            
            // Check if user already exists
            if (userExists(conn, email, username)) {
                request.setAttribute("error", "Email or username already exists");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
            
            // Hash password and create user
            String hashedPassword = hashPassword(password);
            if (createUser(conn, firstName, lastName, username, email, phone, hashedPassword)) {
                conn.commit();
                request.setAttribute("message", "Registration successful! Please login.");
                response.sendRedirect("login.jsp");
            } else {
                conn.rollback();
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            throw new ServletException("Registration failed", e);
        }
    }
    
    private boolean userExists(Connection conn, String email, String username) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE email = ? OR username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, username);
            return stmt.executeQuery().next();
        }
    }
    
    private boolean createUser(Connection conn, String firstName, String lastName, 
                             String username, String email, String phone, 
                             String hashedPassword) throws SQLException {
        String sql = "INSERT INTO students (first_name, last_name, username, email, phone, password_hash) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, username);
            stmt.setString(4, email);
            stmt.setString(5, phone);
            stmt.setString(6, hashedPassword);
            return stmt.executeUpdate() == 1;
        }
    }
    
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

    @Override
    public void destroy() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}