package com.example.demo.controller;

import com.example.demo.entity.Associate;
import com.example.demo.entity.EventLog;
import com.example.demo.repository.AssociateRepository;
import com.example.demo.repository.EventLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EventLogControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EventLogRepository eventLogRepository;
    @Autowired
    private AssociateRepository associateRepository;

    @BeforeEach
    void setUp() {
        eventLogRepository.deleteAll();
        associateRepository.deleteAll();
    }

    @Test
    void testGetAllEventLogs() throws Exception {
        EventLog log = new EventLog();
        log.setEventType("PROMOTION");
        log.setEventDate(LocalDateTime.now());
        log.setTriggeredBy("system");
        log.setOldHierarchy(Collections.singletonMap("level", 1));
        log.setNewHierarchy(Collections.singletonMap("level", 2));
        eventLogRepository.save(log);

        mockMvc.perform(get("/eventlogs/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("PROMOTION"));
    }

    @Test
    void testGetEventLogsByAgent() throws Exception {
        Associate associate = new Associate();
        associate.setId("A20");
        associate.setName("Agent User");
        associate.setLicenceNumber("LIC300");
        associate.setLevel(1);
        associate.setIsActive(true);
        associate.setStartDate(LocalDateTime.now());
        associate.setIsSpecialCase(false);
        associateRepository.save(associate);

        EventLog log = new EventLog();
        log.setEventType("TRANSFER");
        log.setEventDate(LocalDateTime.now());
        log.setTriggeredBy("system");
        log.setOldHierarchy(Collections.singletonMap("branch", "A"));
        log.setNewHierarchy(Collections.singletonMap("branch", "B"));
        log.setAssociate(associate);
        eventLogRepository.save(log);

        mockMvc.perform(get("/eventlogs/agent/A20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("TRANSFER"))
                .andExpect(jsonPath("$[0].associate.id").value("A20"));
    }
}
