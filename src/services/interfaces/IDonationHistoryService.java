package services.interfaces;

import models.DonationHistory;
import java.util.List;

public interface IDonationHistoryService {
    DonationHistory getDonationHistoryById(int donationId);
    List<DonationHistory> getAllDonationHistory();
    void addDonationHistory(DonationHistory history);
    void updateDonationHistory(DonationHistory history);
    void deleteDonationHistory(int donationId);
    List<DonationHistory> getDonationHistoryByDonorId(int donorId);
    boolean hasDonationHistory(int donorId);
}