package services.interfaces;

import models.Donor;
import models.DonorMedicalProfile;
import java.util.List;
import java.util.Date;

public interface IDonorService {
    Donor registerDonor(Donor donor);
    void updateDonor(Donor donor);
    Donor getDonorById(int donorId);
    List<Donor> getAllDonors();
    List<Donor> getDonorsByBloodType(String bloodType);
    void saveMedicalProfile(DonorMedicalProfile profile);
    boolean isDonorEligibleForDonation(int donorId);
    void recordDonation(int donorId, Date donationDate, String location);
}
