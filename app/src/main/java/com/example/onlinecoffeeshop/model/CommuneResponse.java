package com.example.onlinecoffeeshop.model;

import java.util.List;

public class CommuneResponse {
    private String requestId;
    private List<Commune> communes;

    public CommuneResponse() {}

    public CommuneResponse(String requestId, List<Commune> communes) {
        this.requestId = requestId;
        this.communes = communes;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<Commune> getCommunes() {
        return communes;
    }

    public void setCommunes(List<Commune> communes) {
        this.communes = communes;
    }
}
