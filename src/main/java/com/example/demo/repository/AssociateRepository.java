package com.example.demo.repository;

import com.example.demo.entity.Associate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Associate entity, providing CRUD and hierarchy queries.
 */
public interface AssociateRepository extends JpaRepository<Associate, String> {
	/**
	 * Finds all associates who do not have a manager (root associates).
	 * @return list of root associates
	 */
	List<Associate> findByManagerIsNull();

	/**
	 * Finds all associates who report to a given manager.
	 * @param managerId the manager's ID
	 * @return list of associates reporting to the manager
	 */
	List<Associate> findByManager_Id(String managerId);
}
