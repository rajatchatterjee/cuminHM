package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;


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
    /**
     * Gets the branch this associate belongs to.
     * @return the branch
     */
    public Branch getBranch() { return branch; }
    /**
     * Sets the branch for this associate.
     * @param branch the branch
     */
    public void setBranch(Branch branch) { this.branch = branch; }
    /**
     * Gets the associate's ID.
     * @return the ID
     */
    public String getId() { return id; }
    /**
     * Sets the associate's ID.
     * @param id the ID
     */
    public void setId(String id) { this.id = id; }
    /**
     * Gets the associate's name.
     * @return the name
     */
    public String getName() { return name; }
    /**
     * Sets the associate's name.
     * @param name the name
     */
    public void setName(String name) { this.name = name; }
    /**
     * Gets the associate's licence number.
     * @return the licence number
     */
    public String getLicenceNumber() { return licenceNumber; }
    /**
     * Sets the associate's licence number.
     * @param licenceNumber the licence number
     */
    public void setLicenceNumber(String licenceNumber) { this.licenceNumber = licenceNumber; }
    /**
     * Gets the associate's level in the hierarchy.
     * @return the level
     */
    public Integer getLevel() { return level; }
    /**
     * Sets the associate's level in the hierarchy.
     * @param level the level
     */
    public void setLevel(Integer level) { this.level = level; }
    /**
     * Gets whether the associate is active.
     * @return true if active, false otherwise
     */
    public Boolean getIsActive() { return isActive; }
    /**
     * Sets whether the associate is active.
     * @param isActive true if active, false otherwise
     */
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    /**
     * Gets the associate's start date.
     * @return the start date
     */
    public java.time.LocalDateTime getStartDate() { return startDate; }
    /**
     * Sets the associate's start date.
     * @param startDate the start date
     */
    public void setStartDate(java.time.LocalDateTime startDate) { this.startDate = startDate; }
    /**
     * Gets the associate's end date.
     * @return the end date
     */
    public java.time.LocalDateTime getEndDate() { return endDate; }
    /**
     * Sets the associate's end date.
     * @param endDate the end date
     */
    public void setEndDate(java.time.LocalDateTime endDate) { this.endDate = endDate; }
    /**
     * Gets the manager of this associate.
     * @return the manager
     */
    public Associate getManager() { return manager; }
    /**
     * Sets the manager for this associate.
     * @param manager the manager
     */
    public void setManager(Associate manager) { this.manager = manager; }
    /**
     * Gets whether this associate is a special case.
     * @return true if special case, false otherwise
     */
    public Boolean getIsSpecialCase() { return isSpecialCase; }
    /**
     * Sets whether this associate is a special case.
     * @param isSpecialCase true if special case, false otherwise
     */
    public void setIsSpecialCase(Boolean isSpecialCase) { this.isSpecialCase = isSpecialCase; }
    /**
     * Returns the hierarchy chain from this associate up to the root manager as a string.
     * @return the hierarchy chain string
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
