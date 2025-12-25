package ru.mybot.dataprovider;

import ru.mybot.dto.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PostgresDataProvider implements DataProvider {

    private static final String URL = "jdbc:postgresql://localhost:5432/breathtaking";
    private static final String USER = "postgres";
    private static final String PASSWORD = "твой_пароль"; // ← ЗАМЕНИ

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Services

    @Override
    public List<ServiceDTO> getAllServices() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, duration FROM services")) {

            List<ServiceDTO> services = new ArrayList<>();
            while (rs.next()) {
                ServiceDTO s = new ServiceDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("duration")
                );
                services.add(s);
            }
            return services;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public ServiceDTO getServiceById(int id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, name, duration FROM services WHERE id = ?")) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ServiceDTO(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("duration")
                    );
                }
                throw new IllegalArgumentException("Service not found: id=" + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching service", e);
        }
    }

    @Override
    public int createService(ServiceDTO service) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO services (name, duration) VALUES (?, ?) RETURNING id")) {

            stmt.setString(1, service.getName());
            stmt.setInt(2, service.getDuration());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean updateService(ServiceDTO service) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE services SET name = ?, duration = ? WHERE id = ?")) {

            stmt.setString(1, service.getName());
            stmt.setInt(2, service.getDuration());
            stmt.setInt(3, service.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteService(int id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM services WHERE id = ?")) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Appointments
    @Override
    public List<AppointmentDTO> getAllAppointments() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT id, client_id, service_id, master_id, appointment_date, appointment_time, status FROM appointments")) {

            List<AppointmentDTO> appointments = new ArrayList<>();
            while (rs.next()) {
                AppointmentDTO a = new AppointmentDTO();
                a.setId(rs.getInt("id"));
                a.setClientId(rs.getInt("client_id"));
                a.setServiceId(rs.getInt("service_id"));
                a.setMasterId(rs.getInt("master_id"));
                a.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
                a.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
                a.setStatus(rs.getString("status"));
                appointments.add(a);
            }
            return appointments;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public AppointmentDTO getAppointmentById(int id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, client_id, service_id, master_id, appointment_date, appointment_time, status FROM appointments WHERE id = ?")) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AppointmentDTO a = new AppointmentDTO();
                    a.setId(rs.getInt("id"));
                    a.setClientId(rs.getInt("client_id"));
                    a.setServiceId(rs.getInt("service_id"));
                    a.setMasterId(rs.getInt("master_id"));
                    a.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
                    a.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
                    a.setStatus(rs.getString("status"));
                    return a;
                }
                throw new IllegalArgumentException("Appointment not found: id=" + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching appointment", e);
        }
    }

    @Override
    public int createAppointment(AppointmentDTO appointment) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO appointments (client_id, service_id, master_id, appointment_date, appointment_time, status) " +
                             "VALUES (?, ?, ?, ?, ?, ?) RETURNING id")) {

            stmt.setInt(1, appointment.getClientId());
            stmt.setInt(2, appointment.getServiceId());
            stmt.setInt(3, appointment.getMasterId());
            stmt.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setTime(5, Time.valueOf(appointment.getAppointmentTime()));
            stmt.setString(6, appointment.getStatus());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean updateAppointment(AppointmentDTO appointment) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE appointments SET client_id = ?, service_id = ?, master_id = ?, " +
                             "appointment_date = ?, appointment_time = ?, status = ? WHERE id = ?")) {

            stmt.setInt(1, appointment.getClientId());
            stmt.setInt(2, appointment.getServiceId());
            stmt.setInt(3, appointment.getMasterId());
            stmt.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setTime(5, Time.valueOf(appointment.getAppointmentTime()));
            stmt.setString(6, appointment.getStatus());
            stmt.setInt(7, appointment.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteAppointment(int id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM appointments WHERE id = ?")) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // CLients

    @Override
    public Optional<ClientDTO> getClientByTelegramId(long telegramId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, telegram_id, name, phone FROM clients WHERE telegram_id = ?")) {

            stmt.setLong(1, telegramId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ClientDTO c = new ClientDTO();
                    c.setId(rs.getInt("id"));
                    c.setTelegramId(rs.getLong("telegram_id"));
                    c.setName(rs.getString("name"));
                    c.setPhone(rs.getString("phone"));
                    return Optional.of(c);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    @Override
    public int createClient(ClientDTO client) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO clients (telegram_id, name, phone) VALUES (?, ?, ?) RETURNING id")) {

            stmt.setLong(1, client.getTelegramId());
            stmt.setString(2, client.getName());
            stmt.setString(3, client.getPhone());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean updateClient(ClientDTO client) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE clients SET telegram_id = ?, name = ?, phone = ? WHERE id = ?")) {

            stmt.setLong(1, client.getTelegramId());
            stmt.setString(2, client.getName());
            stmt.setString(3, client.getPhone());
            stmt.setInt(4, client.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Masters
    @Override
    public MasterDTO getMasterById(int id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, name, phone, COALESCE(max_clients_per_day, 10) as max_clients_per_day FROM masters WHERE id = ?")) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MasterDTO m = new MasterDTO();
                    m.setId(rs.getInt("id"));
                    m.setName(rs.getString("name"));
                    m.setPhone(rs.getString("phone"));
                    m.setMaxClientsPerDay(rs.getInt("max_clients_per_day"));
                    return m;
                }
                throw new IllegalArgumentException("Master not found: id=" + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching master", e);
        }
    }

    @Override
    public int countAppointmentsByMasterAndDate(int masterId, LocalDate date) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM appointments WHERE master_id = ? AND appointment_date = ? AND status != 'cancelled'")) {

            stmt.setInt(1, masterId);
            stmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Orders
    @Override
    public int createOrder(OrderDTO order) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO orders (appointment_id, service_id, price) VALUES (?, ?, ?) RETURNING id")) {

            stmt.setInt(1, order.getAppointmentId());
            stmt.setInt(2, order.getServiceId());
            stmt.setInt(3, order.getPrice());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<OrderDTO> getOrdersByAppointmentId(int appointmentId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, appointment_id, service_id, price FROM orders WHERE appointment_id = ?")) {

            stmt.setInt(1, appointmentId);
            List<OrderDTO> orders = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderDTO o = new OrderDTO();
                    o.setId(rs.getInt("id"));
                    o.setAppointmentId(rs.getInt("appointment_id"));
                    o.setServiceId(rs.getInt("service_id"));
                    o.setPrice(rs.getInt("price"));
                    orders.add(o);
                }
                return orders;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
