package services;

import models.BloodInventory;
import services.interfaces.IInventoryService;
import utils.interfaces.IConnectionProvider;
import utils.Logger;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class InventoryService implements IInventoryService {
    private final Connection connection;

    public InventoryService(IConnectionProvider connectionProvider) {
        if (connectionProvider == null) {
            throw new IllegalArgumentException("Connection provider cannot be null");
        }
        try {
            this.connection = connectionProvider.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }

    @Override
    public void addInventory(BloodInventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory cannot be null");
        }

        // First check if blood type exists
        String checkQuery = "SELECT inventory_id, units FROM blood_inventory WHERE blood_type = ? AND status = 'Available'";
        String updateQuery = "UPDATE blood_inventory SET units = units + ?, last_updated = ? WHERE blood_type = ? AND status = 'Available'";
        String insertQuery = "INSERT INTO blood_inventory (blood_type, units, status, last_updated) VALUES (?, ?, ?, ?)";
        
        PreparedStatement checkStmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;
        
        try {
            checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, inventory.getBloodType());
            rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Update existing inventory
                updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setInt(1, inventory.getUnits());
                updateStmt.setTimestamp(2, new Timestamp(new Date().getTime()));
                updateStmt.setString(3, inventory.getBloodType());
                updateStmt.executeUpdate();
                inventory.setInventoryId(rs.getInt("inventory_id"));
            } else {
                // Insert new inventory
                insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                insertStmt.setString(1, inventory.getBloodType());
                insertStmt.setInt(2, inventory.getUnits());
                insertStmt.setString(3, "Available");
                insertStmt.setTimestamp(4, new Timestamp(new Date().getTime()));
                insertStmt.executeUpdate();
                
                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    inventory.setInventoryId(generatedKeys.getInt(1));
                }
            }
            Logger.info("Updated blood inventory: " + inventory.getBloodType() + ", Units: " + inventory.getUnits());
        } catch (SQLException e) {
            Logger.error("Error adding blood inventory", e);
            throw new RuntimeException("Error adding blood inventory: " + e.getMessage(), e);
        } finally {
            closeResources(rs, checkStmt, updateStmt, insertStmt);
        }
    }

    // Add this method to properly close resources
    private void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    Logger.error("Error closing resource", e);
                }
            }
        }
    }

    @Override
    public void updateStatus(int inventoryId, String newStatus) {
        String query = "UPDATE blood_inventory SET status = ?, last_updated = ? WHERE inventory_id = ?";
        
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, newStatus);
            stmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            stmt.setInt(3, inventoryId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Inventory item not found with ID: " + inventoryId);
            }
            Logger.info("Updated inventory status: ID " + inventoryId + " to " + newStatus);
        } catch (SQLException e) {
            Logger.error("Error updating inventory status", e);
            throw new RuntimeException("Error updating inventory status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BloodInventory> getAllInventory() {
        List<BloodInventory> inventory = new ArrayList<>();
        String query = "SELECT * FROM blood_inventory ORDER BY blood_type, last_updated DESC";
        
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                inventory.add(extractInventoryFromResultSet(rs));
            }
            return inventory;
        } catch (SQLException e) {
            Logger.error("Error retrieving blood inventory", e);
            throw new RuntimeException("Error retrieving blood inventory: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt);
        }
    }

    @Override
    public BloodInventory getInventoryById(int inventoryId) {
        String query = "SELECT * FROM blood_inventory WHERE inventory_id = ?";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, inventoryId);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return extractInventoryFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            Logger.error("Error retrieving inventory by ID", e);
            throw new RuntimeException("Error retrieving inventory by ID: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt);
        }
    }

    @Override
    public List<BloodInventory> getInventoryByBloodType(String bloodType) {
        List<BloodInventory> inventory = new ArrayList<>();
        String query = "SELECT * FROM blood_inventory WHERE blood_type = ? ORDER BY last_updated DESC";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement(query);
            stmt.setString(1, bloodType.toUpperCase());
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                inventory.add(extractInventoryFromResultSet(rs));
            }
            return inventory;
        } catch (SQLException e) {
            Logger.error("Error retrieving inventory by blood type", e);
            throw new RuntimeException("Error retrieving inventory by blood type: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt);
        }
    }

    @Override
    public Map<String, Integer> getBloodTypeUnits() {
        Map<String, Integer> bloodTypeUnits = new HashMap<>();
        String query = "SELECT blood_type, SUM(units) as total_units FROM blood_inventory " +
                      "WHERE status = 'Available' GROUP BY blood_type";
        
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                bloodTypeUnits.put(rs.getString("blood_type"), rs.getInt("total_units"));
            }
            return bloodTypeUnits;
        } catch (SQLException e) {
            Logger.error("Error retrieving blood type units", e);
            throw new RuntimeException("Error retrieving blood type units: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt);
        }
    }

    @Override
    public void removeInventory(int inventoryId) {
        String query = "DELETE FROM blood_inventory WHERE inventory_id = ?";
        
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, inventoryId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Inventory item not found with ID: " + inventoryId);
            }
            Logger.info("Removed inventory item: ID " + inventoryId);
        } catch (SQLException e) {
            Logger.error("Error removing inventory", e);
            throw new RuntimeException("Error removing inventory: " + e.getMessage(), e);
        } finally {
            closeResources(stmt);
        }
    }

    private BloodInventory extractInventoryFromResultSet(ResultSet rs) throws SQLException {
        BloodInventory inventory = new BloodInventory(
            rs.getString("blood_type"),
            rs.getInt("units"),
            rs.getString("status")
        );
        inventory.setInventoryId(rs.getInt("inventory_id"));
        if (rs.getString("location") != null) {
            inventory.setLocation(rs.getString("location"));
        }
        return inventory;
    }
}
