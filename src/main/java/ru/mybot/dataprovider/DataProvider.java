package ru.mybot.dataprovider;

import ru.mybot.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DataProvider {

    // CRUD для Services
    List<ServiceDTO> getAllServices();
    ServiceDTO getServiceById(int id);
    int createService(ServiceDTO service);
    boolean updateService(ServiceDTO service);
    boolean deleteService(int id);

    // CRUD для Appointment
    List<AppointmentDTO> getAllAppointments();
    AppointmentDTO getAppointmentById(int id);
    int createAppointment(AppointmentDTO appointment);
    boolean updateAppointment(AppointmentDTO appointment);
    boolean deleteAppointment(int id);

    // CRUD для Clients
    Optional<ClientDTO> getClientByTelegramId(long telegramId);
    int createClient(ClientDTO client);
    boolean updateClient(ClientDTO client);

    // Для Masters
    MasterDTO getMasterById(int id);

    int countAppointmentsByMasterAndDate(int masterId, LocalDate date);

    // CRUD для Orders
    int createOrder(OrderDTO order);
    List<OrderDTO> getOrdersByAppointmentId(int appointmentId);
}
