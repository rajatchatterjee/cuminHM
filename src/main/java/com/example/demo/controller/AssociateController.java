

package com.example.demo.controller;

import com.example.demo.entity.Associate;
import com.example.demo.entity.Branch;
import com.example.demo.repository.AssociateRepository;
import com.example.demo.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
/**
 * REST controller for managing associates and their hierarchy operations.
 */
public class AssociateController {
    /**
     * Handles CuminHierarchyException and returns a structured error response.
     */
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

    /**
     * Simplifies the levels of all associates in the hierarchy.
     */
    @Operation(summary = "Simplify associate levels", description = "Recalculates and simplifies the levels of all associates in the hierarchy.")
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

    /**
     * Retrieves all associates.
     * @return list of all associates
     */
    @Operation(summary = "Get all associates", description = "Returns a list of all associates.")
    @ApiResponse(responseCode = "200", description = "List of associates returned successfully.")
    @GetMapping(path = "/")
    public java.util.List<Associate> getAllAssociates() {
        return associateRepository.findAll();
    }

    /**
     * Retrieves an associate by ID.
     * @param id the associate's ID
     * @return the associate, if found
     */
    @Operation(summary = "Get associate by ID", description = "Returns an associate by their ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Associate found."),
        @ApiResponse(responseCode = "404", description = "Associate not found.")
    })
    @GetMapping(path = "/{id}")
    public java.util.Optional<Associate> getAssociateById(@PathVariable String id) {
        return associateRepository.findById(id);
    }

    /**
     * Creates a new associate.
     * @param payload the associate data
     * @return the created associate
     */
    @Operation(summary = "Create associate", description = "Creates a new associate.")
    @ApiResponse(responseCode = "200", description = "Associate created successfully.")
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

    /**
     * Promotes an associate up the hierarchy.
     * @param id the associate's ID
     * @param payload the promotion details
     * @return the promoted associate
     */
    @Operation(summary = "Promote associate", description = "Promotes an associate up the hierarchy.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Associate promoted successfully."),
        @ApiResponse(responseCode = "400", description = "Promotion not allowed.")
    })
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

    /**
     * Demotes an associate to report to a new manager.
     * @param id the associate's ID
     * @param payload the demotion details (new manager ID and change date)
     * @return the demoted associate
     */
    @Operation(summary = "Demote associate", description = "Demotes an associate to report to a new manager.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Associate demoted successfully."),
        @ApiResponse(responseCode = "400", description = "Demotion not allowed.")
    })
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

    /**
     * Transfers an associate to a new branch.
     * @param id the associate's ID
     * @param payload the transfer details (new branch ID and change date)
     * @return the updated associate
     */
    @Operation(summary = "Transfer associate", description = "Transfers an associate to a new branch.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Associate transferred successfully."),
        @ApiResponse(responseCode = "400", description = "Invalid branch ID.")
    })
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
