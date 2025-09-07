package com.example.demo.utils;

import com.example.demo.entity.Associate;
import com.example.demo.entity.Branch;
import com.example.demo.repository.AssociateRepository;
import com.example.demo.repository.BranchRepository;
import com.example.demo.service.EventLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class GenerateData {
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private AssociateRepository associateRepository;
    @Autowired
    private EventLogService eventLogService;

    @PostConstruct
    public void generate() {
        // Create 3 branches
        List<Branch> branches = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Branch branch = new Branch();
            branch.setName("Branch " + i);
            branch.setRegion("Region " + i);
            branches.add(branchRepository.save(branch));
        }

        // Create associates in 3 levels
        List<Associate> level1 = new ArrayList<>();
        List<Associate> level2 = new ArrayList<>();
        List<Associate> level3 = new ArrayList<>();
        int idCounter = 1;
        // Level 1 (top)
        for (int i = 0; i < 2; i++) {
            Associate a = new Associate();
            a.setId("A" + idCounter++);
            a.setName("Top Associate " + i);
            a.setLicenceNumber("LIC" + (100 + i));
            a.setLevel(1);
            a.setIsActive(true);
            a.setStartDate(LocalDateTime.now());
            a.setIsSpecialCase(false);
            a.setBranch(branches.get(i % 3));
            level1.add(associateRepository.save(a));
        }
        // Level 2 (reports to level 1)
        for (int i = 0; i < 3; i++) {
            Associate a = new Associate();
            a.setId("A" + idCounter++);
            a.setName("Mid Associate " + i);
            a.setLicenceNumber("LIC" + (200 + i));
            a.setLevel(2);
            a.setIsActive(true);
            a.setStartDate(LocalDateTime.now());
            a.setIsSpecialCase(false);
            a.setManager(level1.get(i % level1.size()));
            a.setBranch(branches.get((i+1) % 3));
            level2.add(associateRepository.save(a));
        }
        // Level 3 (reports to level 2)
        for (int i = 0; i < 5; i++) {
            Associate a = new Associate();
            a.setId("A" + idCounter++);
            a.setName("Leaf Associate " + i);
            a.setLicenceNumber("LIC" + (300 + i));
            a.setLevel(3);
            a.setIsActive(true);
            a.setStartDate(LocalDateTime.now());
            a.setIsSpecialCase(false);
            a.setManager(level2.get(i % level2.size()));
            a.setBranch(branches.get((i+2) % 3));
            level3.add(associateRepository.save(a));
        }

        // Fire a promote event for one associate
        Associate promoteTarget = level2.get(0);
        eventLogService.promoteAssociate(promoteTarget, promoteTarget.getLevel() + 1, LocalDateTime.now());

        // Fire a transfer event for one associate
        Associate transferTarget = level3.get(0);
        Branch newBranch = branches.get((branches.indexOf(transferTarget.getBranch()) + 1) % 3);
        eventLogService.transferAssociate(transferTarget, newBranch, LocalDateTime.now());
    }
}
