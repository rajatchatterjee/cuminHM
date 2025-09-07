package com.example.demo.controller;

import com.example.demo.entity.EventLog;
import com.example.demo.service.EventLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;

@RestController
@RequestMapping("/eventlogs")
/**
 * REST controller for retrieving event logs.
 */
public class EventLogController {
    private final EventLogService eventLogService;

    public EventLogController(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

    /**
     * Retrieves all event logs.
     * @return list of all event logs
     */
    @Operation(summary = "Get all event logs", description = "Returns a list of all event logs.")
    @ApiResponse(responseCode = "200", description = "List of event logs returned successfully.")
    @GetMapping(path = "/")
    public List<EventLog> getAllEventLogs() {
        return eventLogService.getAllEventLogs();
    }

    /**
     * Retrieves event logs for a specific associate (agent).
     * @param agentId the associate's ID
     * @return list of event logs for the agent
     */
    @Operation(summary = "Get event logs by agent", description = "Returns event logs for a specific associate (agent).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of event logs returned successfully."),
        @ApiResponse(responseCode = "404", description = "Agent not found.")
    })
    @GetMapping(path = "/agent/{agentId}")
    public List<EventLog> getEventLogsByAgent(@org.springframework.web.bind.annotation.PathVariable String agentId) {
        return eventLogService.getEventLogsByAgent(agentId);
    }
}
