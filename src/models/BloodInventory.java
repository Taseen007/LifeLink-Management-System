package models;

import java.util.Date;
import java.util.Arrays;

public class BloodInventory {
    private int inventoryId;
    private String bloodType;
    private int units;
    private String status;
    private Date lastUpdated;
    private String location;
    
    private static final String[] VALID_BLOOD_TYPES = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    public BloodInventory(String bloodType, int units, String status) {
        validateBloodType(bloodType);
        validateUnits(units);
        
        this.bloodType = bloodType.toUpperCase();
        this.units = units;
        this.status = status != null ? status : "Available";
        this.lastUpdated = new Date();
    }

    private void validateBloodType(String bloodType) {
        if (bloodType == null || !Arrays.asList(VALID_BLOOD_TYPES).contains(bloodType.toUpperCase())) {
            throw new IllegalArgumentException("Invalid blood type: " + bloodType);
        }
    }

    private void validateUnits(int units) {
        if (units < 0) {
            throw new IllegalArgumentException("Units cannot be negative");
        }
    }

    // Getters and setters
    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getBloodType() {
        return bloodType;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        validateUnits(units);
        this.units = units;
        this.lastUpdated = new Date();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = new Date();
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location != null ? location : "";
        this.lastUpdated = new Date();
    }
}