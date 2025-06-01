package com.nasa.model;

import java.util.HashMap;
import java.util.Map;

public class NasaModel {
    private Map<String, String> cache;
    private String currentEndpoint;
    private String lastSearchQuery;
    private String lastResponse;

    public NasaModel() {
        this.cache = new HashMap<>();
    }

    public void cacheResponse(String endpoint, String response) {
        cache.put(endpoint, response);
    }

    public String getCachedResponse(String endpoint) {
        return cache.get(endpoint);
    }

    public boolean hasCache(String endpoint) {
        return cache.containsKey(endpoint);
    }

    public void clearCache() {
        cache.clear();
    }

    public void setCurrentEndpoint(String endpoint) {
        this.currentEndpoint = endpoint;
    }

    public String getCurrentEndpoint() {
        return currentEndpoint;
    }

    public void setLastSearchQuery(String query) {
        this.lastSearchQuery = query;
    }

    public String getLastSearchQuery() {
        return lastSearchQuery;
    }

    public void setLastResponse(String response) {
        this.lastResponse = response;
    }

    public String getLastResponse() {
        return lastResponse;
    }
} 