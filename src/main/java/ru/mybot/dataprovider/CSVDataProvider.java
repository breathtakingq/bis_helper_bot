package ru.mybot.dataprovider;

import com.opencsv.*;
import ru.mybot.dto.*;
import java.io.*;
import java.util.*;

public class CSVDataProvider implements DataProvider {
    @Override
    public List<ServiceDTO> getAllServices() {
        List<ServiceDTO> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader("services.csv"))) {
            List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                list.add(new ServiceDTO(
                        Integer.parseInt(rows.get(i)[0]),
                        rows.get(i)[1],
                        Integer.parseInt(rows.get(i)[2])
                ));
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int createAppointment(AppointmentDTO apt) {
        try (CSVWriter writer = new CSVWriter(new FileWriter("appointments.csv", true))) { // true = дописывать в конец
            String[] record = {
                    String.valueOf(apt.getClientId()),
                    apt.getAppointmentDate().toString(),
                    apt.getAppointmentTime().toString()
            };
            writer.writeNext(record);
            return 1; // Условный успех
        } catch (IOException e) { return -1; }
    }

    public void exportAppointmentsToCsv(List<AppointmentDTO> appointments, String filename) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            String[] header = {"ID", "Client ID", "Service ID", "Date", "Time", "Status"};
            writer.writeNext(header);

            for (AppointmentDTO apt : appointments) {
                String[] data = {
                        String.valueOf(apt.getId()),
                        String.valueOf(apt.getClientId()),
                        String.valueOf(apt.getServiceId()),
                        apt.getAppointmentDate().toString(),
                        apt.getAppointmentTime().toString(),
                        apt.getStatus()
                };
                writer.writeNext(data);
            }
            System.out.println("Экспорт успешно выполнен в файл: " + filename);
        } catch (IOException e) {
            System.err.println("Ошибка при экспорте CSV: " + e.getMessage());
        }
    }

    @Override public ServiceDTO getServiceById(int id) { return null; }
    @Override public int createService(ServiceDTO s) { return 0; }
    @Override public boolean updateService(ServiceDTO s) { return false; }
    @Override public boolean deleteService(int id) { return false; }

    @Override public List<AppointmentDTO> getAllAppointments() { return new ArrayList<>(); }
    @Override public AppointmentDTO getAppointmentById(int id) { return null; }
    @Override public boolean updateAppointment(AppointmentDTO a) { return false; }
    @Override public boolean deleteAppointment(int id) { return false; }

    @Override public Optional<ClientDTO> getClientByTelegramId(long id) { return null; }
    @Override public int createClient(ClientDTO c) { return 0; }
    @Override public boolean updateClient(ClientDTO c) { return false; }
    @Override
    public MasterDTO getMasterById(int id) {
        return null;
    }

    @Override
    public int countAppointmentsByMasterAndDate(int masterId, java.time.LocalDate date) {
        return 0;
    }

    @Override
    public int createOrder(OrderDTO order) {
        return -1;
    }

    @Override
    public java.util.List<OrderDTO> getOrdersByAppointmentId(int appointmentId) {
        return new java.util.ArrayList<>();
    }

}
