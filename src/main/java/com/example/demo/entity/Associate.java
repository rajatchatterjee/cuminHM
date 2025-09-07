package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Associate {
    @Override
    public String toString() {
        return String.format("Associate{id='%s', name='%s', licenceNumber='%s', level=%d, isActive=%s, startDate=%s, endDate=%s, isSpecialCase=%s, hierarchy='%s'}",
                id, name, licenceNumber, level, isActive, startDate, endDate, isSpecialCase, getHierarchyChain());
    }
    @Id
    private String id;

    private String name;
    private String licenceNumber;
    private Integer level;
    private Boolean isActive;
    @Column(columnDefinition = "timestamp")
    private java.time.LocalDateTime startDate;
    @Column(columnDefinition = "timestamp")
    private java.time.LocalDateTime endDate;
    // ...existing code...

    @ManyToOne
    @JoinColumn(name = "manager_id")
    @JsonIgnoreProperties({"manager"})
    private Associate manager;

    private Boolean isSpecialCase;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    // Getters and setters
    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLicenceNumber() { return licenceNumber; }
    public void setLicenceNumber(String licenceNumber) { this.licenceNumber = licenceNumber; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public java.time.LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(java.time.LocalDateTime startDate) { this.startDate = startDate; }
    public java.time.LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(java.time.LocalDateTime endDate) { this.endDate = endDate; }
    // ...existing code...
    public Associate getManager() { return manager; }
    public void setManager(Associate manager) { this.manager = manager; }
    public Boolean getIsSpecialCase() { return isSpecialCase; }
    public void setIsSpecialCase(Boolean isSpecialCase) { this.isSpecialCase = isSpecialCase; }
    /**
     * Returns the hierarchy chain from this associate up to the root manager.
     */
    public String getHierarchyChain() {
        StringBuilder sb = new StringBuilder();
        Associate current = this;
        while (current != null) {
            sb.append(current.getName());
            if (current.getManager() != null) {
                sb.append(" -> ");
            }
            current = current.getManager();
        }
        return sb.toString();
    }
}
