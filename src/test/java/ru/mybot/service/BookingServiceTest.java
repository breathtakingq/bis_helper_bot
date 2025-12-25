package ru.mybot.service;

import org.junit.jupiter.api.Test;
import ru.mybot.dataprovider.DataProvider;
import ru.mybot.dto.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingServiceTest {

    @Test
    void shouldReturnErrorWhenServiceNotFound() {
        DataProvider fake = new FakeDataProvider(); // пустые услуги
        BookingService bs = new BookingService(fake);

        String res = bs.createBooking(1L, "2025-12-25", "10:00", "Несуществующая");
        assertTrue(res.toLowerCase().contains("ошибка") || res.toLowerCase().contains("не найдена"));
    }

    @Test
    void slotBusyShouldWork() {
        FakeDataProvider fake = new FakeDataProvider();
        fake.services.add(new ServiceDTO(1, "Маникюр", 60));

        AppointmentDTO a = new AppointmentDTO();
        a.setId(1);
        a.setClientId(1);
        a.setServiceId(1);
        a.setMasterId(1);
        a.setAppointmentDate(LocalDate.of(2025, 12, 25));
        a.setAppointmentTime(LocalTime.of(10, 0));
        a.setStatus("confirmed");
        fake.appointments.add(a);

        BookingService bs = new BookingService(fake);

        boolean busy = bs.isSlotBusy(LocalDate.of(2025, 12, 25), LocalTime.of(10, 30), 60);
        assertTrue(busy);
    }

    @Test
    void shouldCreateBookingSuccessfully() {
        FakeDataProvider fake = new FakeDataProvider();
        fake.services.add(new ServiceDTO(1, "Маникюр", 60));
        fake.masters.add(new MasterDTO(1, "Мастер Иван", "123", 10));

        BookingService bs = new BookingService(fake);
        String result = bs.createBooking(12345L, "2025-12-26", "14:00", "Маникюр");

        assertTrue(result.contains("✅") || result.contains("создана"));
    }

    // Фейк БД
    static class FakeDataProvider implements DataProvider {
        List<ServiceDTO> services = new ArrayList<>();
        List<AppointmentDTO> appointments = new ArrayList<>();
        List<ClientDTO> clients = new ArrayList<>();
        List<MasterDTO> masters = new ArrayList<>();

        @Override
        public List<ServiceDTO> getAllServices() {
            return services;
        }

        @Override
        public ServiceDTO getServiceById(int id) {
            return services.stream().filter(s -> s.getId()==id).findFirst().orElse(null);
        }

        @Override
        public int createService(ServiceDTO service) {
            services.add(service);
            return 1;
        }

        @Override
        public boolean updateService(ServiceDTO service) {
            return false;
        }

        @Override
        public boolean deleteService(int id) {
            return false;
        }

        @Override
        public List<AppointmentDTO> getAllAppointments() {
            return appointments;
        }

        @Override
        public AppointmentDTO getAppointmentById(int id) {
            return null;
        }

        @Override
        public int createAppointment(AppointmentDTO appointment) {
            appointments.add(appointment);
            return 1;
        }

        @Override
        public boolean updateAppointment(AppointmentDTO appointment) {
            return false;
        }

        @Override
        public boolean deleteAppointment(int id) {
            return false;
        }

        @Override
        public Optional<ClientDTO> getClientByTelegramId(long telegramId) {
            return clients.stream().filter(c -> c.getTelegramId()==telegramId).findFirst().orElse(null);
        }

        @Override
        public int createClient(ClientDTO client) {
            client.setId(clients.size() + 1);
            clients.add(client);
            return client.getId();
        }

        @Override
        public boolean updateClient(ClientDTO client) {
            return false;
        }

        @Override
        public MasterDTO getMasterById(int id) {
            return masters.stream().filter(m -> m.getId()==id).findFirst().orElse(null);
        }

        @Override
        public int countAppointmentsByMasterAndDate(int masterId, LocalDate date) {
            return (int) appointments.stream()
                    .filter(a -> a.getMasterId() == masterId && a.getAppointmentDate().equals(date))
                    .count();
        }

        @Override
        public int createOrder(OrderDTO order) {
            return 1;
        }

        @Override
        public List<OrderDTO> getOrdersByAppointmentId(int appointmentId) {
            return List.of();
        }
    }
}
