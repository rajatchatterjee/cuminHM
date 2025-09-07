package com.example.demo.controller;

import com.example.demo.entity.EventLog;
import com.example.demo.service.EventLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/eventlogs")
public class EventLogController {
    private final EventLogService eventLogService;

    public EventLogController(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

    @GetMapping(path = "/")
    public List<EventLog> getAllEventLogs() {
        return eventLogService.getAllEventLogs();
    }

    @GetMapping(path = "/agent/{agentId}")
    public List<EventLog> getEventLogsByAgent(@org.springframework.web.bind.annotation.PathVariable String agentId) {
        return eventLogService.getEventLogsByAgent(agentId);
    }
}
