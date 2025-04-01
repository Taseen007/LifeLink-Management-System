package models;

public class DonorMedicalProfile {
    private int profileId;
    private int donorId;
    private String bloodPressure;
    private double hemoglobin;
    private double weight;
    private double height;
    private String medicalConditions;
    private String medications;

    public DonorMedicalProfile(int donorId, String bloodPressure, double hemoglobin, 
                             double weight, double height, String medicalConditions, 
                             String medications) {
        this.donorId = donorId;
        this.bloodPressure = bloodPressure;
        this.hemoglobin = hemoglobin;
        this.weight = weight;
        this.height = height;
        this.medicalConditions = medicalConditions;
        this.medications = medications;
    }

    // Getters
    public int getProfileId() { return profileId; }
    public int getDonorId() { return donorId; }
    public String getBloodPressure() { return bloodPressure; }
    public double getHemoglobin() { return hemoglobin; }
    public double getWeight() { return weight; }
    public double getHeight() { return height; }
    public String getMedicalConditions() { return medicalConditions; }
    public String getMedications() { return medications; }

    // Setters
    public void setProfileId(int profileId) { this.profileId = profileId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }
    public void setHemoglobin(double hemoglobin) { this.hemoglobin = hemoglobin; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setHeight(double height) { this.height = height; }
    public void setMedicalConditions(String medicalConditions) { this.medicalConditions = medicalConditions; }
    public void setMedications(String medications) { this.medications = medications; }
}