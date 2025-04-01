package services;

import models.Donor;
import models.DonorMedicalProfile;
import services.interfaces.IDonorService;
import utils.interfaces.IConnectionProvider;
import utils.Logger;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class DonorService implements IDonorService {
    private Connection connection;

    public DonorService(IConnectionProvider connectionProvider) throws SQLException {
        this.connection = connectionProvider.getConnection();
    }

    @Override
    public Donor registerDonor(Donor donor) {
        String sql = "INSERT INTO donors (name, contact, email, gender, age, blood_type, location) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, donor.getName());
            stmt.setString(2, donor.getContact());
            stmt.setString(3, donor.getEmail());
            stmt.setString(4, donor.getGender());
            stmt.setInt(5, donor.getAge());
            stmt.setString(6, donor.getBloodType());
            stmt.setString(7, donor.getLocation());
            
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                donor.setDonorId(rs.getInt(1));
            }
            Logger.info("Donor registered: " + donor.getName());
            return donor;
        } catch (SQLException e) {
            Logger.error("Error registering donor", e);
            throw new RuntimeException("Error registering donor", e);
        }
    }

    @Override
    public void updateDonor(Donor donor) {
        String sql = "UPDATE donors SET name=?, contact=?, email=?, gender=?, age=?, blood_type=?, location=? WHERE donor_id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, donor.getName());
            stmt.setString(2, donor.getContact());
            stmt.setString(3, donor.getEmail());
            stmt.setString(4, donor.getGender());
            stmt.setInt(5, donor.getAge());
            stmt.setString(6, donor.getBloodType());
            stmt.setString(7, donor.getLocation());
            stmt.setInt(8, donor.getDonorId());
            stmt.executeUpdate();
            Logger.info("Donor updated: " + donor.getName());
        } catch (SQLException e) {
            Logger.error("Error updating donor", e);
            throw new RuntimeException("Error updating donor", e);
        }
    }

    @Override
    public Donor getDonorById(int donorId) {
        String sql = "SELECT * FROM donors WHERE donor_id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, donorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractDonorFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            Logger.error("Error getting donor", e);
            throw new RuntimeException("Error getting donor", e);
        }
    }

    @Override
    public List<Donor> getAllDonors() {
        List<Donor> donors = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM donors");
            while (rs.next()) {
                donors.add(extractDonorFromResultSet(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error getting donors", e);
            throw new RuntimeException("Error getting donors", e);
        }
        return donors;
    }

    @Override
    public List<Donor> getDonorsByBloodType(String bloodType) {
        return List.of();
    }

    @Override
    public void saveMedicalProfile(DonorMedicalProfile profile) {

    }

    @Override
    public boolean isDonorEligibleForDonation(int donorId) {
        Donor donor = getDonorById(donorId);
        if (donor == null) {
            return false;
        }
        return donor.isEligible(); // Use the Donor model's eligibility check
    }

    @Override
    public void recordDonation(int donorId, Date donationDate, String location) {
        // First update the donor's last donation date
        String updateDonorSql = "UPDATE donors SET last_donation_date = ? WHERE donor_id = ?";
        String insertDonationSql = "INSERT INTO donation_history (donor_id, donation_date, blood_type, location) " +
                                 "SELECT ?, ?, blood_type, ? FROM donors WHERE donor_id = ?";
        
        try {
            connection.setAutoCommit(false);
            try {
                // Update donor's last donation date
                PreparedStatement updateStmt = connection.prepareStatement(updateDonorSql);
                updateStmt.setTimestamp(1, new Timestamp(donationDate.getTime()));
                updateStmt.setInt(2, donorId);
                updateStmt.executeUpdate();

                // Record in donation history
                PreparedStatement insertStmt = connection.prepareStatement(insertDonationSql);
                insertStmt.setInt(1, donorId);
                insertStmt.setTimestamp(2, new Timestamp(donationDate.getTime()));
                insertStmt.setString(3, location);
                insertStmt.setInt(4, donorId);
                insertStmt.executeUpdate();

                connection.commit();
                Logger.info("Donation recorded for donor ID: " + donorId);
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            Logger.error("Error recording donation", e);
            throw new RuntimeException("Error recording donation", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                Logger.error("Error resetting auto-commit", e);
            }
        }
    }

    private Donor extractDonorFromResultSet(ResultSet rs) throws SQLException {
        Donor donor = new Donor(
            rs.getString("name"),
            rs.getString("contact"),
            rs.getString("email"),
            rs.getString("gender"),
            rs.getInt("age"),
            rs.getString("blood_type"),
            rs.getString("location")
        );
        donor.setDonorId(rs.getInt("donor_id"));
        
        // Handle last_donation_date (can be null)
        Timestamp lastDonationDate = rs.getTimestamp("last_donation_date");
        if (lastDonationDate != null) {
            donor.setLastDonationDate(new Date(lastDonationDate.getTime()));
        }
        
        // Set registration date
        Timestamp registrationDate = rs.getTimestamp("registration_date");
        if (registrationDate != null) {
            donor.setRegistrationDate(new Date(registrationDate.getTime()));
        }
        
        // Set eligibility
        donor.setEligible(rs.getBoolean("eligible"));
        
        return donor;
    }
}
