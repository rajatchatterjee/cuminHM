
package com.example.demo.controller;

import com.example.demo.entity.Branch;
import com.example.demo.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/branches")
public class BranchController {
    @Autowired
    private BranchService branchService;

    @PutMapping(path = "/{id}")
    public Branch updateBranch(@PathVariable Long id, @RequestBody Branch updatedBranch) {
        return branchService.updateBranch(id, updatedBranch);
    }

    @PostMapping(path = "/")
    public Branch createBranch(@RequestBody Branch branch) {
        return branchService.createBranch(branch);
    }

    @GetMapping(path = "/")
    public java.util.List<Branch> getAllBranches() {
        return branchService.getAllBranches();
    }

    @GetMapping(path = "/{id}")
    public java.util.Optional<Branch> getBranchById(@PathVariable Long id) {
        return branchService.getBranchById(id);
    }
}
