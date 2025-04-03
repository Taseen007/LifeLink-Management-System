package services;

import models.BloodRequest;
import services.interfaces.IBloodRequestService;
import utils.interfaces.IConnectionProvider;
import utils.Logger;
import java.sql.*;
import java.util.*;

public class BloodRequestService implements IBloodRequestService {
    // Fix the connection provider issue
    private final IConnectionProvider connectionProvider;

    public BloodRequestService(IConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        if (connectionProvider == null) {
            throw new IllegalArgumentException("Connection provider cannot be null");
        }
    }

    // Fix the createRequest method to use int for requestId
    @Override
    public void createRequest(BloodRequest request) {
        String query = "INSERT INTO blood_requests (patient_name, blood_type, units_needed, urgency, hospital, required_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, request.getPatientName());
            stmt.setString(2, request.getBloodType());
            stmt.setInt(3, request.getUnitsNeeded());
            stmt.setString(4, request.getUrgency());
            stmt.setString(5, request.getHospital());
            stmt.setTimestamp(6, new Timestamp(request.getRequiredDate().getTime()));
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                request.setRequestId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating blood request: " + e.getMessage());
        }
    }

    // Fix the updateRequestStatus method to use int
    @Override
    public void updateRequestStatus(int requestId, String newStatus) {
        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException("Invalid status. Status must be 'Pending', 'Approved', 'Cancelled', or 'Fulfilled'");
        }

        String query = "UPDATE blood_requests SET status = ? WHERE request_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connectionProvider.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, newStatus);
            stmt.setInt(2, requestId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new RuntimeException("Request not found with ID: " + requestId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating request status: " + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Logger.error("Error closing statement", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    Logger.error("Error closing connection", e);
                }
            }
        }
    }

    // Add this helper method
    private boolean isValidStatus(String status) {
        return status != null && (
            status.equals("Pending") ||
            status.equals("Approved") ||
            status.equals("Cancelled") ||
            status.equals("Fulfilled")
        );
    }

    // Fix the mapping method name consistency
    // Remove duplicate mapResultSetToRequest method and keep only one mapping method
    private BloodRequest mapResultSetToBloodRequest(ResultSet rs) throws SQLException {
        BloodRequest request = new BloodRequest(
            rs.getString("patient_name"),
            rs.getString("blood_type"),
            rs.getInt("units_needed"),
            rs.getString("urgency"),
            rs.getString("hospital"),
            rs.getTimestamp("required_date")
        );
        request.setRequestId(rs.getInt("request_id"));
        request.setStatus(rs.getString("status"));
        return request;
    }

    // Update these methods to use the correct mapping method name
    @Override
    public List<BloodRequest> getAllRequests() {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM blood_requests";
        try (Connection conn = connectionProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                requests.add(mapResultSetToBloodRequest(rs));  // Changed from mapResultSetToRequest
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching blood requests: " + e.getMessage());
        }
        return requests;
    }

    @Override
    public BloodRequest getRequestById(int requestId) {
        String sql = "SELECT * FROM blood_requests WHERE request_id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBloodRequest(rs);  // Changed from mapResultSetToRequest
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching blood request: " + e.getMessage());
        }
        return null;
    }

    // Fix deleteRequest method to use int and connectionProvider
    @Override
    public void deleteRequest(int requestId) {  // Only keep this version
        String query = "DELETE FROM blood_requests WHERE request_id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, requestId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting request: " + e.getMessage());
        }
    }

    // Remove this duplicate method
    // @Override
    // public void deleteRequest(String requestId) { ... }
    @Override
    public List<BloodRequest> getRequestsByBloodType(String bloodType) {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM blood_requests WHERE blood_type = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bloodType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToBloodRequest(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching blood requests by type: " + e.getMessage());
        }
        return requests;
    }

    @Override
    public List<BloodRequest> getRequestsByUrgency(String urgency) {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM blood_requests WHERE urgency = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, urgency);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToBloodRequest(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching blood requests by urgency: " + e.getMessage());
        }
        return requests;
    }

    @Override
    public Map<String, Integer> getPendingRequestsByBloodType() {
        Map<String, Integer> pendingRequests = new HashMap<>();
        String sql = "SELECT blood_type, COUNT(*) as count FROM blood_requests WHERE status = 'Pending' GROUP BY blood_type";
        try (Connection conn = connectionProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pendingRequests.put(rs.getString("blood_type"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching pending requests: " + e.getMessage());
        }
        return pendingRequests;
    }


    @Override
    public void updateRequest(BloodRequest request) {
        String query = "UPDATE blood_requests SET patient_name = ?, blood_type = ?, " +
                      "units_needed = ?, urgency = ?, hospital = ?, required_date = ? " +
                      "WHERE request_id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, request.getPatientName());
            stmt.setString(2, request.getBloodType());
            stmt.setInt(3, request.getUnitsNeeded());
            stmt.setString(4, request.getUrgency());
            stmt.setString(5, request.getHospital());
            stmt.setTimestamp(6, new Timestamp(request.getRequiredDate().getTime()));
            stmt.setInt(7, request.getRequestId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating blood request: " + e.getMessage());
        }
    }
}