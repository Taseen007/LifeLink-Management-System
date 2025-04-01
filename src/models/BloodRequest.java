package models;

import java.util.Date;
import java.util.Arrays;

public class BloodRequest {
    private String requestId;
    private String patientName;
    private String bloodType;
    private int unitsNeeded;
    private String urgency;
    private String hospital;
    private String status;
    private Date requestDate;
    private Date requiredDate;

    public BloodRequest(String patientName, String bloodType, int unitsNeeded, 
                       String urgency, String hospital, Date requiredDate) {
        validateRequest(patientName, bloodType, unitsNeeded, urgency, hospital);
        this.patientName = patientName;
        this.bloodType = bloodType;
        this.unitsNeeded = unitsNeeded;
        this.urgency = urgency;
        this.hospital = hospital;
        this.status = "Pending";
        this.requestDate = new Date();
        this.requiredDate = requiredDate;
    }

    private void validateRequest(String patientName, String bloodType, 
                               int unitsNeeded, String urgency, String hospital) {
        if (patientName == null || patientName.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name cannot be empty");
        }
        if (!isValidBloodType(bloodType)) {
            throw new IllegalArgumentException("Invalid blood type");
        }
        if (unitsNeeded <= 0) {
            throw new IllegalArgumentException("Units needed must be greater than 0");
        }
        if (urgency == null || !isValidUrgency(urgency)) {
            throw new IllegalArgumentException("Invalid urgency level");
        }
        if (hospital == null || hospital.trim().isEmpty()) {
            throw new IllegalArgumentException("Hospital name cannot be empty");
        }
    }

    private boolean isValidBloodType(String bloodType) {
        String[] validTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        return Arrays.asList(validTypes).contains(bloodType.toUpperCase());
    }

    private boolean isValidUrgency(String urgency) {
        String[] validUrgency = {"Emergency", "High", "Medium", "Low"};
        return Arrays.asList(validUrgency).contains(urgency);
    }

    // Getters
    public String getRequestId() { return requestId; }
    public String getPatientName() { return patientName; }
    public String getBloodType() { return bloodType; }
    public int getUnitsNeeded() { return unitsNeeded; }
    public String getUrgency() { return urgency; }
    public String getHospital() { return hospital; }
    public String getStatus() { return status; }
    public Date getRequestDate() { return requestDate; }
    public Date getRequiredDate() { return requiredDate; }

    // Setters
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setStatus(String status) { this.status = status; }

    // Business logic methods
    public boolean isUrgent() {
        return urgency.equals("Emergency") || urgency.equals("High");
    }

    public boolean isPending() {
        return status.equals("Pending");
    }

    public void updateStatus(String newStatus) {
        if (Arrays.asList("Pending", "Approved", "Fulfilled", "Rejected").contains(newStatus)) {
            this.status = newStatus;
        } else {
            throw new IllegalArgumentException("Invalid status");
        }
    }

    @Override
    public String toString() {
        return String.format("Blood Request [ID: %s, Patient: %s, Type: %s, " +
                           "Units: %d, Urgency: %s, Status: %s]",
                           requestId, patientName, bloodType, unitsNeeded, 
                           urgency, status);
    }
}