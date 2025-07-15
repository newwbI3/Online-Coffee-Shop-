package com.example.onlinecoffeeshop.model;

public class Commune {
    private String code;
    private String name;
    private String englishName;
    private String administrativeLevel;
    private String provinceCode;
    private String provinceName;
    private String decree;

    public Commune() {}

    public Commune(String code, String name, String administrativeLevel, String provinceCode, String provinceName) {
        this.code = code;
        this.name = name;
        this.administrativeLevel = administrativeLevel;
        this.provinceCode = provinceCode;
        this.provinceName = provinceName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getAdministrativeLevel() {
        return administrativeLevel;
    }

    public void setAdministrativeLevel(String administrativeLevel) {
        this.administrativeLevel = administrativeLevel;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getDecree() {
        return decree;
    }

    public void setDecree(String decree) {
        this.decree = decree;
    }

    // For backward compatibility
    public String getLevel() {
        return administrativeLevel;
    }

    public void setLevel(String level) {
        this.administrativeLevel = level;
    }

    public String getDistrictCode() {
        return ""; // Not used in simplified structure
    }

    public void setDistrictCode(String districtCode) {
        // Not used in simplified structure
    }
}
