package com.megacitycab.models;

import java.io.Serializable;
import java.sql.Timestamp;

public class SupportTicket implements Serializable {
    private int ticketId;
    private int userId;
    private String subject;
    private String description;
    private String status;
    private Timestamp createdAt;


    public SupportTicket(int ticketId, int userId, String subject, String description, String status, Timestamp createdAt) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }


    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
