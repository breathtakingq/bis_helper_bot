package ru.mybot.dto;

import jakarta.persistence.*;

@Entity
@Table(name = "masters")
public class MasterDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String phone;

    @Column(name = "max_clients_per_day")
    private int maxClientsPerDay;

    public MasterDTO() {
    }

    public MasterDTO(int id, String name, String phone, int maxClientsPerDay) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.maxClientsPerDay = maxClientsPerDay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMaxClientsPerDay() {
        return maxClientsPerDay;
    }

    public void setMaxClientsPerDay(int maxClientsPerDay) {
        this.maxClientsPerDay = maxClientsPerDay;
    }
}
