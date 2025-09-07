

package com.example.demo.controller;

import com.example.demo.entity.Associate;
import com.example.demo.entity.Branch;
import com.example.demo.repository.AssociateRepository;
import com.example.demo.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.EventLogService;
import com.example.demo.service.AssociateService;
import com.example.demo.dto.PromotionPayload;
import com.example.demo.dto.TransferPayload;
import com.example.demo.dto.AssociatePayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.example.demo.exception.CuminHierarchyException;


@RestController
@RequestMapping("/associates")
public class AssociateController {
    @ExceptionHandler(CuminHierarchyException.class)
    public ResponseEntity<Object> handleCuminHierarchyException(CuminHierarchyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(java.util.Map.of(
                "error", ex.getMessage(),
                "code", ex.getErrorCode()
            ));
    }

    @Autowired
    private AssociateService associateService;

    @PostMapping("/simplifyLevels")
    public void simplifyLevels() {
        associateService.simplifyLevels();
    }
    
    @Autowired
    private AssociateRepository associateRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private EventLogService eventLogService;

    @GetMapping(path = "/")
    public java.util.List<Associate> getAllAssociates() {
        return associateRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public java.util.Optional<Associate> getAssociateById(@PathVariable String id) {
        return associateRepository.findById(id);
    }

    @PostMapping(path = "/")
    public Associate createAssociate(@RequestBody AssociatePayload payload) {
        Associate associate = new Associate();
        associate.setId(payload.id);
        associate.setName(payload.name);
        associate.setLicenceNumber(payload.licenceNumber);
        associate.setLevel(payload.level);
        associate.setIsActive(payload.isActive);
        associate.setStartDate(payload.startDate);
        if (payload.endDate == null) {
            associate.setEndDate(null);
        } else {
            associate.setEndDate(payload.endDate);
        }

        associate.setIsSpecialCase(payload.isSpecialCase);
        if (payload.managerId != null) {
            associate.setManager(associateRepository.findById(payload.managerId).orElse(null));
        }
        if (payload.branchId != null) {
            try {
                Long branchIdLong = Long.valueOf(payload.branchId);
                associate.setBranch(branchRepository.findById(branchIdLong).orElse(null));
            } catch (NumberFormatException e) {
                associate.setBranch(null);
            }
        }
        return associateRepository.save(associate);
    }

    @PostMapping(path = "/{id}/promote")
    public Associate promoteAssociate(@PathVariable String id, @RequestBody PromotionPayload payload) {
        Associate associate = associateRepository.findById(id).orElseThrow();
    eventLogService.promoteAssociate(associate, payload.newLevel, payload.changeDate);
        return associateRepository.save(associate);
    }

    // Demotion now expects a payload with newManagerId and changeDate
    public static class DemotionPayload {
        public String newManagerId;
        public java.time.LocalDateTime changeDate;
    }

    @PostMapping(path = "/{id}/demote")
    public Associate demoteAssociate(@PathVariable String id, @RequestBody DemotionPayload payload) {
        Associate associate = associateRepository.findById(id).orElseThrow();
        if (payload.newManagerId == null) {
            throw new CuminHierarchyException("newManagerId is required for demotion.", "DEMOTION_MANAGER_ID_REQUIRED");
        }
        Associate newManager = associateRepository.findById(payload.newManagerId)
            .orElseThrow(() -> new CuminHierarchyException("New manager not found: " + payload.newManagerId, "DEMOTION_MANAGER_NOT_FOUND"));
        eventLogService.demoteAssociate(associate, newManager, payload.changeDate);
        return associateRepository.save(associate);
    }

    @PostMapping(path = "/{id}/transfer")
    public Associate transferAssociate(@PathVariable String id, @RequestBody TransferPayload payload) {
        Associate associate = associateRepository.findById(id).orElseThrow();
        Branch branch = null;
        try {
            Long branchIdLong = Long.valueOf(payload.newBranchId);
            branch = branchRepository.findById(branchIdLong).orElseThrow();
        } catch (NumberFormatException e) {
            throw new CuminHierarchyException("Invalid branch ID: " + payload.newBranchId, "TRANSFER_INVALID_BRANCH_ID");
        }
    eventLogService.transferAssociate(associate, branch, payload.changeDate);
        return associateRepository.save(associate);
    }

    // DTO classes moved to com.example.demo.dto
}
