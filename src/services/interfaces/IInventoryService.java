package services.interfaces;

import models.BloodInventory;
import java.util.List;
import java.util.Map;

public interface IInventoryService {
    void addInventory(BloodInventory inventory);
    void updateStatus(int inventoryId, String newStatus);
    List<BloodInventory> getAllInventory();
    BloodInventory getInventoryById(int inventoryId);
    List<BloodInventory> getInventoryByBloodType(String bloodType);
    Map<String, Integer> getBloodTypeUnits();
    void removeInventory(int inventoryId);
}
