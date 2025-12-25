package ru.mybot.dto;

import jakarta.persistence.*;

@Entity
@Table(name = "clients")
public class ClientDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "telegram_id")
    private long telegramId;

    private String name;

    private String phone;

    public ClientDTO(int id, long telegramId, String name, String phone) {
        this.id = id;
        this.telegramId = telegramId;
        this.name = name;
        this.phone = phone;
    }

    public ClientDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(long telegramId) {
        this.telegramId = telegramId;
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
}
