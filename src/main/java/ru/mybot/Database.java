package ru.mybot;

import java.sql.*;
import java.util.*;

public class Database {
    private String url = "jdbc:postgresql://localhost:5432/mybot_db";
    private String user = "breathtaking";
    private String password = "botpass";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public List<Service> getServices() {
        List<Service> services = new ArrayList<>();

        try (Connection conn = connect()) {
            String sql = "SELECT id, name, duration FROM services";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Service service = new Service();
                service.id = rs.getInt("id");
                service.name = rs.getString("name");
                service.duration = rs.getInt("duration");
                services.add(service);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка БД: " + e.getMessage());
        }

        return services;
    }

    public int addService(int masterId, String name, int duration) {
        try (Connection conn = connect()) {
            String sql = "INSERT INTO services (master_id, name, duration) VALUES (?, ?, ?) RETURNING id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, masterId);
            stmt.setString(2, name);
            stmt.setInt(3, duration);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка БД: " + e.getMessage());
        }
        return -1;
    }
}
