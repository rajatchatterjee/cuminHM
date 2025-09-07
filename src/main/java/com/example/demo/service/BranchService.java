package com.example.demo.service;

import com.example.demo.entity.Branch;
import com.example.demo.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BranchService {
    @Autowired
    private BranchRepository branchRepository;

    public Branch updateBranch(Long id, Branch updatedBranch) {
        return branchRepository.findById(id)
                .map(branch -> {
                    branch.setName(updatedBranch.getName());
                    branch.setRegion(updatedBranch.getRegion());
                    return branchRepository.save(branch);
                })
                .orElseThrow();
    }

    public Branch createBranch(Branch branch) {
        return branchRepository.save(branch);
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
    }
}
