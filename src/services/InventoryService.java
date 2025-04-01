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

        String query = "INSERT INTO blood_inventory (blood_type, units, status, last_updated) VALUES (?, ?, ?, ?)";
        
        try {
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, inventory.getBloodType());
            stmt.setInt(2, inventory.getUnits());
            stmt.setString(3, inventory.getStatus());
            stmt.setTimestamp(4, new Timestamp(inventory.getLastUpdated().getTime()));
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                inventory.setInventoryId(rs.getInt(1));
            }
            Logger.info("Added blood inventory: " + inventory.getBloodType() + ", Units: " + inventory.getUnits());
        } catch (SQLException e) {
            Logger.error("Error adding blood inventory", e);
            throw new RuntimeException("Error adding blood inventory: " + e.getMessage(), e);
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
        
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                inventory.add(extractInventoryFromResultSet(rs));
            }
            return inventory;
        } catch (SQLException e) {
            Logger.error("Error retrieving blood inventory", e);
            throw new RuntimeException("Error retrieving blood inventory: " + e.getMessage(), e);
        }
    }

    @Override
    public BloodInventory getInventoryById(int inventoryId) {
        String query = "SELECT * FROM blood_inventory WHERE inventory_id = ?";
        
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, inventoryId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractInventoryFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            Logger.error("Error retrieving inventory by ID", e);
            throw new RuntimeException("Error retrieving inventory by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BloodInventory> getInventoryByBloodType(String bloodType) {
        List<BloodInventory> inventory = new ArrayList<>();
        String query = "SELECT * FROM blood_inventory WHERE blood_type = ? ORDER BY last_updated DESC";
        
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, bloodType.toUpperCase());
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                inventory.add(extractInventoryFromResultSet(rs));
            }
            return inventory;
        } catch (SQLException e) {
            Logger.error("Error retrieving inventory by blood type", e);
            throw new RuntimeException("Error retrieving inventory by blood type: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Integer> getBloodTypeUnits() {
        Map<String, Integer> bloodTypeUnits = new HashMap<>();
        String query = "SELECT blood_type, SUM(units) as total_units FROM blood_inventory " +
                      "WHERE status = 'Available' GROUP BY blood_type";
        
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                bloodTypeUnits.put(rs.getString("blood_type"), rs.getInt("total_units"));
            }
            return bloodTypeUnits;
        } catch (SQLException e) {
            Logger.error("Error retrieving blood type units", e);
            throw new RuntimeException("Error retrieving blood type units: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeInventory(int inventoryId) {
        String query = "DELETE FROM blood_inventory WHERE inventory_id = ?";
        
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, inventoryId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Inventory item not found with ID: " + inventoryId);
            }
            Logger.info("Removed inventory item: ID " + inventoryId);
        } catch (SQLException e) {
            Logger.error("Error removing inventory", e);
            throw new RuntimeException("Error removing inventory: " + e.getMessage(), e);
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
