package com.example.demo.service;

import com.example.demo.entity.EventLog;
import com.example.demo.repository.EventLogRepository;
import com.example.demo.entity.Associate;
import com.example.demo.entity.Branch;
import com.example.demo.exception.CuminHierarchyException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service for logging and managing associate hierarchy events such as promotion, demotion, and transfer.
 *
 * <p>
 * Promotion: Moves an associate up the hierarchy by setting their manager to their current manager's manager and decreasing their level by 1.
 * Demotion: Moves an associate down the hierarchy by assigning a new manager and setting their level to one more than the new manager's level.
 * Transfer: Changes the branch of an associate and logs the event.
 * </p>
 */
@Service
public class EventLogService {
    @Autowired
    private EventLogRepository eventLogRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieves all event logs.
     * @return list of all event logs
     */
    public List<EventLog> getAllEventLogs() {
        return eventLogRepository.findAll();
    }

    /**
     * Retrieves all event logs for a specific associate (agent).
     * @param agentId the associate's ID
     * @return list of event logs for the agent
     */
    public List<EventLog> getEventLogsByAgent(String agentId) {
        return eventLogRepository.findByAssociate_Id(agentId);
    }

    /**
     * Logs an event for an associate, including hierarchy changes.
     * @param eventType the type of event (Promotion, Demotion, Transfer)
     * @param associate the associate involved
     * @param oldHierarchy the hierarchy before the event
     * @param newHierarchy the hierarchy after the event
     * @param eventDate the date of the event
     */
    @Transactional
    public void logEvent(String eventType, Associate associate, Object oldHierarchy, Object newHierarchy, java.time.LocalDateTime eventDate) {
        EventLog log = new EventLog();
        log.setEventType(eventType);
        log.setAssociate(associate);
        log.setEventDate(eventDate);
        log.setOldHierarchy(oldHierarchy);
        log.setNewHierarchy(newHierarchy);
        log.setTriggeredBy("system");
        entityManager.persist(log);
    }

    /**
     * Promotes an associate up the hierarchy.
     * <p>
     * The associate's manager is set to their current manager's manager (moving them up one level),
     * and their level is decreased by 1. Throws an exception if already at the top level.
     * </p>
     * @param associate the associate to promote
     * @param newLevel the new level (not used in logic, for compatibility)
     * @param changeDate the date of the promotion
     * @throws CuminHierarchyException if the associate is already at the top level
     */
    @Transactional
    public void promoteAssociate(Associate associate, int newLevel, java.time.LocalDateTime changeDate) {
        if (associate.getLevel() == null || associate.getLevel() <= 1) {
            throw new CuminHierarchyException("Associate is already at the top level and cannot be promoted further.", "PROMOTION_INVALID");
        }
        Object oldHierarchyObj = getHierarchyChain(associate);
        // Move up the hierarchy: set manager to manager's manager, reduce level by 1
        Associate currentManager = associate.getManager();
        if (currentManager != null) {
            associate.setManager(currentManager.getManager());
        }
        associate.setLevel(associate.getLevel() - 1);
        Object newHierarchyObj = getHierarchyChain(associate);
        logEvent("Promotion", associate, oldHierarchyObj, newHierarchyObj, changeDate);
    }

    /**
     * Demotes an associate to report to a new manager and adjusts their level.
     * <p>
     * The associate's manager is set to the specified new manager, and their level is set to one more than the new manager's level.
     * Validates that the new manager is at the same or a lower position (numerically higher level).
     * </p>
     * @param associate the associate being demoted
     * @param newManager the new manager
     * @param changeDate the date of the demotion
     * @throws CuminHierarchyException if validation fails
     */
    @Transactional
    public void demoteAssociate(Associate associate, Associate newManager, java.time.LocalDateTime changeDate) {
        if (newManager == null) {
            throw new CuminHierarchyException("New manager for demotion not found.", "DEMOTION_MANAGER_NOT_FOUND");
        }
        if (associate.getId().equals(newManager.getId())) {
            throw new CuminHierarchyException("Associate cannot be their own manager.", "DEMOTION_SELF_MANAGER");
        }
        if (associate.getLevel() == null || newManager.getLevel() == null) {
            throw new CuminHierarchyException("Level information missing for associate or new manager.", "DEMOTION_LEVEL_MISSING");
        }
        // New manager must be at a level equal to or lower (numerically higher) than the associate
        if (newManager.getLevel() < associate.getLevel()) {
            throw new CuminHierarchyException("New manager must be at the same or a lower position in the hierarchy (numerically higher level).", "DEMOTION_INVALID_MANAGER_LEVEL");
        }
        Object oldHierarchyObj = getHierarchyChain(associate);
        // Always set the associate's manager to the new manager
        associate.setManager(newManager);
        associate.setLevel(newManager.getLevel() + 1);
        Object newHierarchyObj = getHierarchyChain(associate);
        logEvent("Demotion", associate, oldHierarchyObj, newHierarchyObj, changeDate);
    }

    /**
     * Transfers an associate to a new branch and logs the event.
     * @param associate the associate being transferred
     * @param newBranch the new branch
     * @param changeDate the date of the transfer
     */
    @Transactional
    public void transferAssociate(Associate associate, Branch newBranch, java.time.LocalDateTime changeDate) {
        Object oldHierarchyObj = getHierarchyChain(associate);
        associate.setBranch(newBranch);
        Object newHierarchyObj = getHierarchyChain(associate);
        logEvent("Transfer", associate, oldHierarchyObj, newHierarchyObj, changeDate);
    }

    /**
     * Returns the chain of managers from the given associate up to the root.
     * @param associate the starting associate
     * @return list representing the hierarchy chain
     */
    private java.util.List<Associate> getHierarchyChain(Associate associate) {
        java.util.List<Associate> chain = new java.util.ArrayList<>();
        Associate current = associate;
        while (current != null) {
            chain.add(current);
            current = current.getManager();
        }
        return chain;
    }
}
