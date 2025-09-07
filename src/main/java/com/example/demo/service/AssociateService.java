package com.example.demo.service;

import com.example.demo.entity.Associate;
import com.example.demo.repository.AssociateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service for managing associates and their hierarchy levels.
 */
@Service
public class AssociateService {
    @Autowired
    private AssociateRepository associateRepository;

    /**
     * Simplifies the levels of all associates in the hierarchy by traversing from root associates and assigning levels recursively.
     */
    public void simplifyLevels() {
        List<Associate> roots = associateRepository.findByManagerIsNull();
        for (Associate root : roots) {
            assignLevelsRecursively(root, 1);
        }
    }

    /**
     * Recursively assigns levels to an associate and all their reports.
     * @param associate the associate to assign level to
     * @param level the level to assign
     */
    private void assignLevelsRecursively(Associate associate, int level) {
        associate.setLevel(level);
        associateRepository.save(associate);
        List<Associate> reports = associateRepository.findByManager_Id(associate.getId());
        for (Associate report : reports) {
            assignLevelsRecursively(report, level + 1);
        }
    }
}
