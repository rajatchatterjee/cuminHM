package com.example.demo.controller;

import com.example.demo.entity.Branch;
import com.example.demo.repository.BranchRepository;
import com.example.demo.repository.AssociateRepository;
import com.example.demo.repository.EventLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BranchControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private AssociateRepository associateRepository;
    @Autowired
    private EventLogRepository eventLogRepository;

    @BeforeEach
    void setUp() {
        eventLogRepository.deleteAll();
        associateRepository.deleteAll();
        branchRepository.deleteAll();
    }

    @Test
    void testCreateBranch() throws Exception {
        String json = "{" +
                "\"name\": \"Main Branch\"," +
                "\"region\": \"North\"}";
    mockMvc.perform(post("/branches/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Main Branch"))
                .andExpect(jsonPath("$.region").value("North"));
    }

    @Test
    void testGetAllBranches() throws Exception {
        Branch b1 = new Branch();
        b1.setName("A");
        b1.setRegion("East");
        branchRepository.save(b1);
        Branch b2 = new Branch();
        b2.setName("B");
        b2.setRegion("West");
        branchRepository.save(b2);
    mockMvc.perform(get("/branches/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("A", "B")));
    }

    @Test
    void testGetBranchById() throws Exception {
        Branch b = new Branch();
        b.setName("Test");
        b.setRegion("South");
        Branch saved = branchRepository.save(b);
    mockMvc.perform(get("/branches/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.region").value("South"));
    }

    @Test
    void testUpdateBranch() throws Exception {
        Branch b = new Branch();
        b.setName("Old");
        b.setRegion("OldRegion");
        Branch saved = branchRepository.save(b);
        String json = "{" +
                "\"name\": \"New\"," +
                "\"region\": \"NewRegion\"}";
    mockMvc.perform(put("/branches/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.region").value("NewRegion"));
    }
}
