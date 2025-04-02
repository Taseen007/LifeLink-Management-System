package ui;

import services.DonorService;
import services.InventoryService;
import services.interfaces.IDonorService;
import services.interfaces.IInventoryService;
import utils.DatabaseConnection;
import utils.interfaces.IConnectionProvider;

class ServiceInitializer {
    public ServiceContainer initializeServices() throws Exception {
        try {
            IConnectionProvider dbConnection = new DatabaseConnection();
            IDonorService donorService = new DonorService(dbConnection);
            IInventoryService inventoryService = new InventoryService(dbConnection);

            return new ServiceContainer(dbConnection, donorService, inventoryService);
        } catch (Exception e) {
            throw new Exception("Failed to initialize services: " + e.getMessage(), e);
        }
    }
}