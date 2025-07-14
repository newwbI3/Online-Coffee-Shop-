package com.example.onlinecoffeeshop.model;

public class Province {
    private String code;
    private String name;
    private String englishName;
    private String administrativeLevel;
    private String decree;

    public Province() {}

    public Province(String code, String name, String administrativeLevel) {
        this.code = code;
        this.name = name;
        this.administrativeLevel = administrativeLevel;
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

    @Override
    public String toString() {
        return name;
    }
}
