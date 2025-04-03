package models;

import java.util.Date;

public class DonationHistory {
    private int donationId;
    private int donorId;
    private Date donationDate;
    private String bloodType;
    private int units;
    private String location;

    public DonationHistory(int donorId, String bloodType, String location) {
        this.donorId = donorId;
        this.bloodType = bloodType;
        this.location = location;
        this.donationDate = new Date();
        this.units = 1;
    }

    // Getters
    public int getDonationId() { return donationId; }
    public int getDonorId() { return donorId; }
    public Date getDonationDate() { return donationDate; }
    public String getBloodType() { return bloodType; }
    public int getUnits() { return units; }
    public String getLocation() { return location; }

    // Setters
    public void setDonationId(int donationId) { this.donationId = donationId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }
    public void setDonationDate(Date donationDate) { this.donationDate = donationDate; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public void setUnits(int units) { this.units = units; }
    public void setLocation(String location) { this.location = location; }
}