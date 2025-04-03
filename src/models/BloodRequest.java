package models;

import java.util.Date;
import java.util.Arrays;

public class BloodRequest {
    private int requestId;  // Changed from String to int
    private String patientName;
    private String bloodType;
    private int unitsNeeded;
    private String urgency;
    private String hospital;
    private Date requestDate;
    private Date requiredDate;
    private String status;

    // Update getter to match new type
    public int getRequestId() { return requestId; }
    
    // Update setter to match new type
    public void setRequestId(int requestId) { this.requestId = requestId; }
    
    // Add status validation
    public void setStatus(String status) {
        if (status != null && (status.equals("Pending") || 
            status.equals("Approved") || 
            status.equals("Fulfilled") || 
            status.equals("Rejected"))) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Invalid status");
        }
    }
    public String getPatientName() { return patientName; }
    public String getBloodType() { return bloodType; }
    public int getUnitsNeeded() { return unitsNeeded; }
    public String getUrgency() { return urgency; }
    public String getHospital() { return hospital; }
    public Date getRequestDate() { return requestDate; }
    public Date getRequiredDate() { return requiredDate; }
    public String getStatus() { return status; }
    
    // Add this constructor to match the database mapping
    public BloodRequest(String patientName, String bloodType, int unitsNeeded,
                        String urgency, String hospital, Date requiredDate) {
        this.patientName = patientName;
        this.bloodType = bloodType;
        this.unitsNeeded = unitsNeeded;
        this.urgency = urgency;
        this.hospital = hospital;
        this.requiredDate = requiredDate;
        this.requestDate = new Date(); // Set current date as request date
        this.status = "Pending"; // Set default status
    }

    // Move setters outside the constructor
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
    
    public void setUnitsNeeded(int unitsNeeded) {
        this.unitsNeeded = unitsNeeded;
    }
    
    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }
    
    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
    
    public void setRequiredDate(Date requiredDate) {
        this.requiredDate = requiredDate;
    }
}