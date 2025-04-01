package models;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;

public class DonationHistory {
    private int donorId;
    private Date lastDonationDate;
    private int totalDonations;
    private String location;
    private List<DonationRecord> donationRecords;
    private SimpleDateFormat dateFormat;

    public DonationHistory(int donorId, String location) {
        if (donorId <= 0) {
            throw new IllegalArgumentException("Invalid donor ID");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }
        
        this.donorId = donorId;
        this.location = location;
        this.totalDonations = 0;
        this.donationRecords = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void recordDonation(String date) {
        try {
            Date donationDate = dateFormat.parse(date);
            if (isEligibleForDonation(donationDate)) {
                DonationRecord record = new DonationRecord(donationDate, location);
                donationRecords.add(record);
                this.lastDonationDate = donationDate;
                this.totalDonations++;
            } else {
                throw new IllegalStateException("Donor not eligible for donation yet");
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
        }
    }

    private boolean isEligibleForDonation(Date newDonationDate) {
        if (lastDonationDate == null) return true;
        
        // Check if 3 months (90 days) have passed since last donation
        long diffInMillies = newDonationDate.getTime() - lastDonationDate.getTime();
        long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
        return diffInDays >= 90;
    }

    // Getters
    public int getDonorId() { return donorId; }
    public Date getLastDonationDate() { return lastDonationDate; }
    public int getTotalDonations() { return totalDonations; }
    public String getLocation() { return location; }
    public List<DonationRecord> getDonationHistory() { 
        return new ArrayList<>(donationRecords); 
    }

    // Inner class to store individual donation records
    private static class DonationRecord {
        private Date donationDate;
        private String location;
        private String status;

        public DonationRecord(Date donationDate, String location) {
            this.donationDate = donationDate;
            this.location = location;
            this.status = "Completed";
        }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return String.format("Donation on %s at %s - %s", 
                sdf.format(donationDate), location, status);
        }
    }

    public boolean hasDonatedBefore() {
        return totalDonations > 0;
    }

    public String getLastDonationDateFormatted() {
        return lastDonationDate != null ? dateFormat.format(lastDonationDate) : "No donations yet";
    }

    @Override
    public String toString() {
        return String.format("Donation History [Donor ID: %d, Total Donations: %d, " +
                           "Last Donation: %s, Location: %s]",
                           donorId, totalDonations, getLastDonationDateFormatted(), location);
    }
}