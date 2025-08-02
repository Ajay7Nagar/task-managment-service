package com.talentica.taskmanagement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Api(tags = "Health Check", description = "Health check endpoints")
public class HealthController {

    @GetMapping
    @ApiOperation(value = "Health check", notes = "Check if the service is running")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Task Management Service");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
}