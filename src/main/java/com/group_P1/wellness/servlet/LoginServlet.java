package com.group_P1.wellness.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Base64;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username").trim();
        String password = request.getParameter("password").trim();

        // Initial validation
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Username and password are required");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        if (username.length() < 4) {
            request.setAttribute("error", "Username must be at least 4 characters");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        if (password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5353/PRG381_wellness", 
                    "postgres", 
                    "Ven06246"
            );

            // Hash the password
            String hashedPassword = hashPassword(password);

            // Prepare SQL statement with parameterized query
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT username, password_hash, first_name FROM students WHERE username = ? AND password_hash = ?"
            );
            ps.setString(1, username);
            ps.setString(2, hashedPassword);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Successful login
                HttpSession session = request.getSession();
                session.setAttribute("studentName", rs.getString("first_name"));
                session.setAttribute("username", rs.getString("username"));
                response.sendRedirect("dashboard.jsp");
            } else {
                // Failed login
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

            conn.close();
        } catch (ClassNotFoundException e) {
            request.setAttribute("systemError", "Database driver not found");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("systemError", "Database connection error");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("systemError", "An unexpected error occurred");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    // Password hashing method
    public static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}