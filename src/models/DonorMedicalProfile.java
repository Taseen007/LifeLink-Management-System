package models;

import java.util.Date;

public class DonorMedicalProfile {
    private int profileId;
    private int donorId;
    private String bloodType;
    private double weight;
    private String medicalHistory;
    private boolean isEligible;
    private Date lastCheckupDate;

    public DonorMedicalProfile(int donorId, String bloodType, double weight, 
                             String medicalHistory, boolean isEligible, 
                             Date lastCheckupDate) {
        this.donorId = donorId;
        this.bloodType = bloodType;
        this.weight = weight;
        this.medicalHistory = medicalHistory;
        this.isEligible = isEligible;
        this.lastCheckupDate = lastCheckupDate;
    }

    // Getters
    public int getProfileId() { return profileId; }
    public int getDonorId() { return donorId; }
    public String getBloodType() { return bloodType; }
    public double getWeight() { return weight; }
    public String getMedicalHistory() { return medicalHistory; }
    public boolean isEligible() { return isEligible; }
    public Date getLastCheckupDate() { return lastCheckupDate; }

    // Setters
    public void setProfileId(int profileId) { this.profileId = profileId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public void setEligible(boolean eligible) { isEligible = eligible; }
    public void setLastCheckupDate(Date lastCheckupDate) { this.lastCheckupDate = lastCheckupDate; }
}