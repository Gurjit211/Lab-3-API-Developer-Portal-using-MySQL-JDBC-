import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

public class ApiKeyServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("user");

        try {
            Connection connection = getConnection();
            if ("generate".equals(action)) {
                String apiKey = generateApiKey();
                String query = "INSERT INTO api_keys (user_id, api_key, status, created_at) VALUES ((SELECT id FROM users WHERE username = ?), ?, 'active', NOW())";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, apiKey);
                stmt.executeUpdate();
                response.sendRedirect("dashboard.html");
            } else if ("deactivate".equals(action)) {
                String keyId = request.getParameter("keyId");
                String query = "UPDATE api_keys SET status = 'inactive' WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, keyId);
                stmt.executeUpdate();
                response.sendRedirect("api_keys.html");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("API Key operation failed: " + e.getMessage());
        }
    }

    private String generateApiKey() {
        return "API" + System.currentTimeMillis();
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/api_portal", "root", "password");
        } catch (Exception e) {
            throw new SQLException("Database connection failed", e);
        }
    }
}
