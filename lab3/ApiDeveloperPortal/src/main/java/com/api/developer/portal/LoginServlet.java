package com.api.developer.portal;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Handles user login via HTTP POST requests.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get input parameters from the request
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validate inputs (ensure they are not null or empty)
        if (username == null || username.isEmpty() ||
            password == null || password.isEmpty()) {
            response.getWriter().write("Error: All fields are required.");
            return;
        }

        // Authenticate user by checking the database
        boolean isValidUser = false;
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, password);

                try (ResultSet resultSet = statement.executeQuery()) {
                    isValidUser = resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Database error: " + e.getMessage());
            return;
        }

        if (isValidUser) {
            // Create a new session for the authenticated user
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            response.getWriter().write("Login successful!");
        } else {
            response.getWriter().write("Invalid username or password.");
        }
    }

    /**
     * Handles HTTP GET requests (optional, not used here).
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }
}