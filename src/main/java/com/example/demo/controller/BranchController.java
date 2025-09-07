
package com.example.demo.controller;

import com.example.demo.entity.Branch;
import com.example.demo.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/branches")
/**
 * REST controller for managing branches.
 */
public class BranchController {
    @Autowired
    private BranchService branchService;

    /**
     * Updates a branch by ID.
     * @param id the branch ID
     * @param updatedBranch the updated branch data
     * @return the updated branch
     */
    @Operation(summary = "Update branch", description = "Updates a branch by its ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Branch updated successfully."),
        @ApiResponse(responseCode = "404", description = "Branch not found.")
    })
    @PutMapping(path = "/{id}")
    public Branch updateBranch(@PathVariable Long id, @RequestBody Branch updatedBranch) {
        return branchService.updateBranch(id, updatedBranch);
    }

    /**
     * Creates a new branch.
     * @param branch the branch data
     * @return the created branch
     */
    @Operation(summary = "Create branch", description = "Creates a new branch.")
    @ApiResponse(responseCode = "200", description = "Branch created successfully.")
    @PostMapping(path = "/")
    public Branch createBranch(@RequestBody Branch branch) {
        return branchService.createBranch(branch);
    }

    /**
     * Retrieves all branches.
     * @return list of all branches
     */
    @Operation(summary = "Get all branches", description = "Returns a list of all branches.")
    @ApiResponse(responseCode = "200", description = "List of branches returned successfully.")
    @GetMapping(path = "/")
    public java.util.List<Branch> getAllBranches() {
        return branchService.getAllBranches();
    }

    /**
     * Retrieves a branch by ID.
     * @param id the branch ID
     * @return the branch, if found
     */
    @Operation(summary = "Get branch by ID", description = "Returns a branch by its ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Branch found."),
        @ApiResponse(responseCode = "404", description = "Branch not found.")
    })
    @GetMapping(path = "/{id}")
    public java.util.Optional<Branch> getBranchById(@PathVariable Long id) {
        return branchService.getBranchById(id);
    }
}
