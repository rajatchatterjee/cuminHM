package com.example.demo.controller;

import com.example.demo.entity.Associate;
import com.example.demo.repository.AssociateRepository;
import com.example.demo.repository.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.repository.EventLogRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AssociateControllerTest {
        @Test
        void testSimplifyLevels() throws Exception {
                // Create hierarchy: root -> mid -> leaf
                Associate root = new Associate();
                root.setId("R1");
                root.setName("Root");
                root.setLicenceNumber("LICROOT");
                root.setIsActive(true);
                root.setStartDate(LocalDateTime.now());
                root.setIsSpecialCase(false);
                associateRepository.save(root);

                Associate mid = new Associate();
                mid.setId("M1");
                mid.setName("Mid");
                mid.setLicenceNumber("LICMID");
                mid.setIsActive(true);
                mid.setStartDate(LocalDateTime.now());
                mid.setIsSpecialCase(false);
                mid.setManager(root);
                associateRepository.save(mid);

                Associate leaf = new Associate();
                leaf.setId("L1");
                leaf.setName("Leaf");
                leaf.setLicenceNumber("LICLEAF");
                leaf.setIsActive(true);
                leaf.setStartDate(LocalDateTime.now());
                leaf.setIsSpecialCase(false);
                leaf.setManager(mid);
                associateRepository.save(leaf);

                mockMvc.perform(post("/associates/simplifyLevels"))
                                .andExpect(status().isOk());

                // Validate levels
                Associate updatedRoot = associateRepository.findById("R1").orElseThrow();
                Associate updatedMid = associateRepository.findById("M1").orElseThrow();
                Associate updatedLeaf = associateRepository.findById("L1").orElseThrow();
                org.junit.jupiter.api.Assertions.assertEquals(1, updatedRoot.getLevel());
                org.junit.jupiter.api.Assertions.assertEquals(2, updatedMid.getLevel());
                org.junit.jupiter.api.Assertions.assertEquals(3, updatedLeaf.getLevel());
        }
        @Test
        void testGetAllAssociates() throws Exception {
                Associate a1 = new Associate();
                a1.setId("A20");
                a1.setName("User One");
                a1.setLicenceNumber("LIC301");
                a1.setLevel(1);
                a1.setIsActive(true);
                a1.setStartDate(LocalDateTime.now());
                a1.setIsSpecialCase(false);
                associateRepository.save(a1);

                Associate a2 = new Associate();
                a2.setId("A21");
                a2.setName("User Two");
                a2.setLicenceNumber("LIC302");
                a2.setLevel(2);
                a2.setIsActive(true);
                a2.setStartDate(LocalDateTime.now());
                a2.setIsSpecialCase(false);
                associateRepository.save(a2);

                mockMvc.perform(get("/associates/"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value("A20"))
                                .andExpect(jsonPath("$[1].id").value("A21"));
        }

        @Test
        void testGetAssociateById() throws Exception {
                Associate a = new Associate();
                a.setId("A22");
                a.setName("User Three");
                a.setLicenceNumber("LIC303");
                a.setLevel(3);
                a.setIsActive(true);
                a.setStartDate(LocalDateTime.now());
                a.setIsSpecialCase(false);
                associateRepository.save(a);

                mockMvc.perform(get("/associates/A22"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("A22"))
                                .andExpect(jsonPath("$.name").value("User Three"));
        }
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private AssociateRepository associateRepository;
        @Autowired
        private BranchRepository branchRepository;
        @Autowired
        private EventLogRepository eventLogRepository;

        @BeforeEach
        void setUp() {
                eventLogRepository.deleteAll();
                associateRepository.deleteAll();
                branchRepository.deleteAll();
        }

    @Test
    void testCreateAssociate() throws Exception {
        String json = "{" +
                "\"id\": \"A10\"," +
                "\"name\": \"Test User\"," +
                "\"licenceNumber\": \"LIC200\"," +
                "\"level\": 2," +
                "\"isActive\": true," +
                "\"startDate\": \"2025-09-05T00:00:00\"," +
                "\"endDate\": null," +
                "\"lastChangeDate\": null," +
                "\"isSpecialCase\": false," +
                "\"managerId\": null," +
                "\"branchId\": null}";
        mockMvc.perform(post("/associates/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("A10"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.licenceNumber").value("LIC200"));
    }

    @Test
    void testPromoteAssociate_AlreadyAtLevel1_ShouldReturnError() throws Exception {
        Associate associate = new Associate();
        associate.setId("A11");
        associate.setName("Promote Me");
        associate.setLicenceNumber("LIC201");
        associate.setLevel(1);
        associate.setIsActive(true);
        associate.setStartDate(LocalDateTime.now());
        associate.setIsSpecialCase(false);
        associateRepository.save(associate);

        String json = "{" +
                "\"newLevel\": 1," +
                "\"changeDate\": \"2025-09-06T10:00:00\"}";
        mockMvc.perform(post("/associates/A11/promote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testPromoteAssociate_ValidPromotion() throws Exception {
        // root (level 1)
        Associate root = new Associate();
        root.setId("R2");
        root.setName("Root2");
        root.setLicenceNumber("LICROOT2");
        root.setLevel(1);
        root.setIsActive(true);
        root.setStartDate(LocalDateTime.now());
        root.setIsSpecialCase(false);
        associateRepository.save(root);

        // mid (level 2, manager=root)
        Associate mid = new Associate();
        mid.setId("M2");
        mid.setName("Mid2");
        mid.setLicenceNumber("LICMID2");
        mid.setLevel(2);
        mid.setIsActive(true);
        mid.setStartDate(LocalDateTime.now());
        mid.setIsSpecialCase(false);
        mid.setManager(root);
        associateRepository.save(mid);

        String json = "{" +
                "\"newLevel\": 1," +
                "\"changeDate\": \"2025-09-06T10:00:00\"}";
        mockMvc.perform(post("/associates/M2/promote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(1));

        // Validate manager is now root's manager (null)
        Associate promoted = associateRepository.findById("M2").orElseThrow();
        org.junit.jupiter.api.Assertions.assertNull(promoted.getManager());
    }

    @Test
    void testDemoteAssociate_ValidDemotion() throws Exception {
        // Manager at level 2
        Associate manager = new Associate();
        manager.setId("MGR1");
        manager.setName("Manager");
        manager.setLicenceNumber("LICMGR1");
        manager.setLevel(2);
        manager.setIsActive(true);
        manager.setStartDate(LocalDateTime.now());
        manager.setIsSpecialCase(false);
        associateRepository.save(manager);

        // Associate at level 2 (should be able to demote to report to manager at same level)
        Associate associate = new Associate();
        associate.setId("A12");
        associate.setName("Demote Me");
        associate.setLicenceNumber("LIC202");
        associate.setLevel(2);
        associate.setIsActive(true);
        associate.setStartDate(LocalDateTime.now());
        associate.setIsSpecialCase(false);
        associateRepository.save(associate);

        String json = "{" +
                "\"newManagerId\": \"MGR1\"," +
                "\"changeDate\": \"2025-09-06T11:00:00\"}";
        mockMvc.perform(post("/associates/A12/demote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(3))
                .andExpect(jsonPath("$.manager.id").value("MGR1"));
    }

    @Test
    void testDemoteAssociate_InvalidManagerLevel() throws Exception {
        // Manager at level 1 (higher up)
        Associate manager = new Associate();
        manager.setId("MGR2");
        manager.setName("Manager2");
        manager.setLicenceNumber("LICMGR2");
        manager.setLevel(1);
        manager.setIsActive(true);
        manager.setStartDate(LocalDateTime.now());
        manager.setIsSpecialCase(false);
        associateRepository.save(manager);

        // Associate at level 2
        Associate associate = new Associate();
        associate.setId("A13");
        associate.setName("Demote Me2");
        associate.setLicenceNumber("LIC203");
        associate.setLevel(2);
        associate.setIsActive(true);
        associate.setStartDate(LocalDateTime.now());
        associate.setIsSpecialCase(false);
        associateRepository.save(associate);

        String json = "{" +
                "\"newManagerId\": \"MGR2\"," +
                "\"changeDate\": \"2025-09-06T11:00:00\"}";
        mockMvc.perform(post("/associates/A13/demote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testDemoteAssociate_SelfDemotion_ShouldFail() throws Exception {
        Associate associate = new Associate();
        associate.setId("A14");
        associate.setName("Self Demote");
        associate.setLicenceNumber("LIC204");
        associate.setLevel(2);
        associate.setIsActive(true);
        associate.setStartDate(LocalDateTime.now());
        associate.setIsSpecialCase(false);
        associateRepository.save(associate);

        String json = "{" +
                "\"newManagerId\": \"A14\"," +
                "\"changeDate\": \"2025-09-06T11:00:00\"}";
        mockMvc.perform(post("/associates/A14/demote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testDemoteAssociate_MissingManager_ShouldFail() throws Exception {
        Associate associate = new Associate();
        associate.setId("A15");
        associate.setName("Missing Manager");
        associate.setLicenceNumber("LIC205");
        associate.setLevel(2);
        associate.setIsActive(true);
        associate.setStartDate(LocalDateTime.now());
        associate.setIsSpecialCase(false);
        associateRepository.save(associate);

        String json = "{" +
                "\"newManagerId\": \"DOESNOTEXIST\"," +
                "\"changeDate\": \"2025-09-06T11:00:00\"}";
        mockMvc.perform(post("/associates/A15/demote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testTransferAssociate() throws Exception {
        // Create a branch
        var branch = new com.example.demo.entity.Branch();
        branch.setName("Transfer Branch");
        branch.setRegion("West");
        var savedBranch = branchRepository.save(branch);

        // Create an associate
        Associate associate = new Associate();
        associate.setId("A13");
        associate.setName("Transfer Me");
        associate.setLicenceNumber("LIC203");
        associate.setLevel(2);
        associate.setIsActive(true);
        associate.setStartDate(LocalDateTime.now());
        associate.setIsSpecialCase(false);
        associateRepository.save(associate);

        String json = "{" +
                "\"newBranchId\": \"" + savedBranch.getId() + "\"," +
                "\"changeDate\": \"2025-09-06T12:00:00\"}";
        mockMvc.perform(post("/associates/A13/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branch.id").value(savedBranch.getId()));
    }
}
