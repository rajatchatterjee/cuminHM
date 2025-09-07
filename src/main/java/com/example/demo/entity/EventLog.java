package com.example.demo.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;


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
    /**
     * Gets the event log ID.
     * @return the ID
     */
    public Long getId() { return id; }
    /**
     * Sets the event log ID.
     * @param id the ID
     */
    public void setId(Long id) { this.id = id; }
    /**
     * Gets the event type.
     * @return the event type
     */
    public String getEventType() { return eventType; }
    /**
     * Sets the event type.
     * @param eventType the event type
     */
    public void setEventType(String eventType) { this.eventType = eventType; }
    /**
     * Gets the event date.
     * @return the event date
     */
    public java.time.LocalDateTime getEventDate() { return eventDate; }
    /**
     * Sets the event date.
     * @param eventDate the event date
     */
    public void setEventDate(java.time.LocalDateTime eventDate) { this.eventDate = eventDate; }
    /**
     * Gets the user or system that triggered the event.
     * @return the trigger source
     */
    public String getTriggeredBy() { return triggeredBy; }
    /**
     * Sets the user or system that triggered the event.
     * @param triggeredBy the trigger source
     */
    public void setTriggeredBy(String triggeredBy) { this.triggeredBy = triggeredBy; }
    /**
     * Gets the old hierarchy (before the event).
     * @return the old hierarchy object
     */
    public Object getOldHierarchy() { return oldHierarchy; }
    /**
     * Sets the old hierarchy (before the event).
     * @param oldHierarchy the old hierarchy object
     */
    public void setOldHierarchy(Object oldHierarchy) { this.oldHierarchy = oldHierarchy; }
    /**
     * Gets the new hierarchy (after the event).
     * @return the new hierarchy object
     */
    public Object getNewHierarchy() { return newHierarchy; }
    /**
     * Sets the new hierarchy (after the event).
     * @param newHierarchy the new hierarchy object
     */
    public void setNewHierarchy(Object newHierarchy) { this.newHierarchy = newHierarchy; }
    /**
     * Gets the associate related to this event log.
     * @return the associate
     */
    public Associate getAssociate() { return associate; }
    /**
     * Sets the associate related to this event log.
     * @param associate the associate
     */
    public void setAssociate(Associate associate) { this.associate = associate; }
}
