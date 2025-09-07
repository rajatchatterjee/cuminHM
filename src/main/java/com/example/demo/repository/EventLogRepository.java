package com.example.demo.repository;

import com.example.demo.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for EventLog entity, providing CRUD and event log queries.
 */
public interface EventLogRepository extends JpaRepository<EventLog, Long> {
	/**
	 * Finds all event logs for a given associate (agent).
	 * @param agentId the associate's ID
	 * @return list of event logs for the agent
	 */
	java.util.List<EventLog> findByAssociate_Id(String agentId);
}
