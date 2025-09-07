package com.example.demo.repository;

import com.example.demo.entity.Associate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssociateRepository extends JpaRepository<Associate, String> {
	List<Associate> findByManagerIsNull();
	List<Associate> findByManager_Id(String managerId);
}
