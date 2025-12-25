package ru.mybot.utils;

public class Constants {

    // Services
    public static final String SQL_GET_SERVICES =
            "SELECT id, name, duration FROM services";

    public static final String SQL_GET_SERVICE_BY_ID =
            "SELECT id, name, duration FROM services WHERE id = ?";

    public static final String SQL_ADD_SERVICE =
            "INSERT INTO services (master_id, name, duration) VALUES (?, ?, ?) RETURNING id";

    // Clients
    public static final String SQL_GET_CLIENT_BY_TELEGRAM_ID =
            "SELECT id, telegram_id, name, phone FROM clients WHERE telegram_id = ?";

    public static final String SQL_ADD_CLIENT =
            "INSERT INTO clients (telegram_id, name, phone) VALUES (?, ?, ?) RETURNING id";

    // Appointments
    public static final String SQL_GET_APPOINTMENTS =
            "SELECT id, client_id, service_id, appointment_date, appointment_time, status FROM appointments";

    public static final String SQL_ADD_APPOINTMENT =
            "INSERT INTO appointments (client_id, service_id, master_id, appointment_date, appointment_time, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

    // Masters
    public static final String SQL_GET_MASTER_BY_ID =
            "SELECT id, name, phone, max_clients_per_day FROM masters WHERE id = ?";

    public static final String SQL_COUNT_APPOINTMENTS_BY_MASTER_AND_DATE =
            "SELECT COUNT(*) AS cnt FROM appointments " +
                    "WHERE master_id = ? AND appointment_date = ? AND status <> 'cancelled'";

    // Orders
    public static final String SQL_CREATE_ORDER =
            "INSERT INTO orders (appointment_id, service_id, price) VALUES (?, ?, ?) RETURNING id";

    public static final String SQL_GET_ORDERS_BY_APPOINTMENT =
            "SELECT id, appointment_id, service_id, price FROM orders WHERE appointment_id = ?";

    // Status заказа
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_CANCELLED = "cancelled";
}
