package services;

import models.DonationHistory;
import services.interfaces.IDonationHistoryService;
import utils.interfaces.IConnectionProvider;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonationHistoryService implements IDonationHistoryService {
    private final IConnectionProvider connectionProvider;

    public DonationHistoryService(IConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public List<DonationHistory> getAllDonationHistory() {
        List<DonationHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM donation_history";
        
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                DonationHistory history = new DonationHistory(
                    rs.getInt("donor_id"),
                    rs.getString("blood_type"),
                    rs.getString("location")
                );
                history.setDonationId(rs.getInt("donation_id"));
                history.setDonationDate(rs.getTimestamp("donation_date"));
                history.setUnits(rs.getInt("units"));
                histories.add(history);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching donation histories: " + e.getMessage());
        }
        return histories;
    }

    @Override
    public DonationHistory getDonationHistoryById(int donationId) {
        String sql = "SELECT * FROM donation_history WHERE donation_id = ?";
        
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, donationId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                DonationHistory history = new DonationHistory(
                    rs.getInt("donor_id"),
                    rs.getString("blood_type"),
                    rs.getString("location")
                );
                history.setDonationId(rs.getInt("donation_id"));
                history.setDonationDate(rs.getTimestamp("donation_date"));
                history.setUnits(rs.getInt("units"));
                return history;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching donation history: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void addDonationHistory(DonationHistory history) {
        String sql = "INSERT INTO donation_history (donor_id, blood_type, units, location) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, history.getDonorId());
            stmt.setString(2, history.getBloodType());
            stmt.setInt(3, history.getUnits());
            stmt.setString(4, history.getLocation());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding donation history: " + e.getMessage());
        }
    }

    @Override
    public void updateDonationHistory(DonationHistory history) {
        String sql = "UPDATE donation_history SET blood_type = ?, units = ?, location = ? WHERE donation_id = ?";
        
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, history.getBloodType());
            stmt.setInt(2, history.getUnits());
            stmt.setString(3, history.getLocation());
            stmt.setInt(4, history.getDonationId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating donation history: " + e.getMessage());
        }
    }

    @Override
    public List<DonationHistory> getDonationHistoryByDonorId(int donorId) {
        List<DonationHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM donation_history WHERE donor_id = ?";
        
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, donorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                DonationHistory history = new DonationHistory(
                    rs.getInt("donor_id"),
                    rs.getString("blood_type"),
                    rs.getString("location")
                );
                history.setDonationId(rs.getInt("donation_id"));
                history.setDonationDate(rs.getTimestamp("donation_date"));
                history.setUnits(rs.getInt("units"));
                histories.add(history);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching donor's donation history: " + e.getMessage());
        }
        return histories;
    }

    @Override
    public void deleteDonationHistory(int donorId) {
        String sql = "DELETE FROM donation_history WHERE donor_id = ?";
        
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, donorId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting donation history: " + e.getMessage());
        }
    }

    @Override
    public boolean hasDonationHistory(int donorId) {
        String sql = "SELECT COUNT(*) FROM donation_history WHERE donor_id = ?";
        
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, donorId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking donation history: " + e.getMessage());
        }
        return false;
    }
}