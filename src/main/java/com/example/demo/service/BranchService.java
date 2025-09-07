package com.example.demo.service;

import com.example.demo.entity.Branch;
import com.example.demo.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing branches.
 */
@Service
public class BranchService {
    @Autowired
    private BranchRepository branchRepository;

    /**
     * Updates a branch by ID.
     * @param id the branch ID
     * @param updatedBranch the updated branch data
     * @return the updated branch
     */
    public Branch updateBranch(Long id, Branch updatedBranch) {
        return branchRepository.findById(id)
                .map(branch -> {
                    branch.setName(updatedBranch.getName());
                    branch.setRegion(updatedBranch.getRegion());
                    return branchRepository.save(branch);
                })
                .orElseThrow();
    }

    /**
     * Creates a new branch.
     * @param branch the branch data
     * @return the created branch
     */
    public Branch createBranch(Branch branch) {
        return branchRepository.save(branch);
    }

    /**
     * Retrieves all branches.
     * @return list of all branches
     */
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    /**
     * Retrieves a branch by ID.
     * @param id the branch ID
     * @return the branch, if found
     */
    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
    }
}
