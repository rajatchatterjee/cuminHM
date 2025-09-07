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

@Service
public class EventLogService {
    @Autowired
    private EventLogRepository eventLogRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<EventLog> getAllEventLogs() {
        return eventLogRepository.findAll();
    }

    public List<EventLog> getEventLogsByAgent(String agentId) {
        return eventLogRepository.findByAssociate_Id(agentId);
    }

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
     * Promotion: Update the level and last_change_date of the existing Associate object.
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
     * Demotion: Move the associate down the hierarchy by assigning a new manager and adjusting the level.
     * @param associate The associate being demoted
     * @param newManager The associate who will be the new manager
     * @param changeDate The date of the demotion
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
     * Transfer: Update the branch and last_change_date of the existing Associate object.
     */
    @Transactional
    public void transferAssociate(Associate associate, Branch newBranch, java.time.LocalDateTime changeDate) {
        Object oldHierarchyObj = getHierarchyChain(associate);
        associate.setBranch(newBranch);
        Object newHierarchyObj = getHierarchyChain(associate);
        logEvent("Transfer", associate, oldHierarchyObj, newHierarchyObj, changeDate);
    }

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
