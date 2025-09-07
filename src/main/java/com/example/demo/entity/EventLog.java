package com.example.demo.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;

@Entity
public class EventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventType;
    @Column(columnDefinition = "timestamp")
    private java.time.LocalDateTime eventDate;
    private String triggeredBy = "system";

    @Type(value = JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Object oldHierarchy; // JSON object


    @Type(value = JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Object newHierarchy; // JSON object

    @ManyToOne
    private Associate associate;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public java.time.LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(java.time.LocalDateTime eventDate) { this.eventDate = eventDate; }
    public String getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(String triggeredBy) { this.triggeredBy = triggeredBy; }
    public Object getOldHierarchy() { return oldHierarchy; }
    public void setOldHierarchy(Object oldHierarchy) { this.oldHierarchy = oldHierarchy; }
    public Object getNewHierarchy() { return newHierarchy; }
    public void setNewHierarchy(Object newHierarchy) { this.newHierarchy = newHierarchy; }
    public Associate getAssociate() { return associate; }
    public void setAssociate(Associate associate) { this.associate = associate; }
}
