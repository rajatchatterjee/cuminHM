package com.example.demo.repository;

import com.example.demo.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Branch entity, providing CRUD operations.
 */
public interface BranchRepository extends JpaRepository<Branch, Long> {
}
