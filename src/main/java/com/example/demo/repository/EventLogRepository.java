package com.example.demo.repository;

import com.example.demo.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {
	java.util.List<EventLog> findByAssociate_Id(String agentId);
}
