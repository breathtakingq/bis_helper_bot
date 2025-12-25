package ru.mybot.dto;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class OrderDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "appointment_id")
    private int appointmentId;

    @Column(name = "service_id")
    private int serviceId;

    private int price;

    public OrderDTO() {
    }

    public OrderDTO(int id, int appointmentId, int serviceId, int price) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.serviceId = serviceId;
        this.price = price;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
