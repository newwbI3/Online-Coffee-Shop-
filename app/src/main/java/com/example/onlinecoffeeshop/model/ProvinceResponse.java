package com.example.onlinecoffeeshop.model;

import java.util.List;

public class ProvinceResponse {
    private String requestId;
    private List<Province> provinces;

    public ProvinceResponse() {}

    public ProvinceResponse(String requestId, List<Province> provinces) {
        this.requestId = requestId;
        this.provinces = provinces;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<Province> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<Province> provinces) {
        this.provinces = provinces;
    }
}
