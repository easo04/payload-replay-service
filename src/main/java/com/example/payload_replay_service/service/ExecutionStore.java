package com.example.payload_replay_service.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExecutionStore {

    private final Map<String, Object> store = new ConcurrentHashMap<>();

    public void save(String executionId, Object data) {
        store.put(executionId, data);
    }

    public Object get(String executionId) {
        return store.get(executionId);
    }
}