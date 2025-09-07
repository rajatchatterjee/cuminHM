package com.example.demo.service;

import com.example.demo.entity.EventLog;
import com.example.demo.repository.EventLogRepository;
import com.example.demo.entity.Associate;
import com.example.demo.entity.Branch;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper = new ObjectMapper();
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
            throw new IllegalArgumentException("Associate is already at the top level and cannot be promoted further.");
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
     * Demotion: Update the level and last_change_date of the existing Associate object.
     */
    @Transactional
    public void demoteAssociate(Associate associate, int newLevel, java.time.LocalDateTime changeDate) {
        Object oldHierarchyObj = getHierarchyChain(associate);
        associate.setLevel(newLevel);
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
