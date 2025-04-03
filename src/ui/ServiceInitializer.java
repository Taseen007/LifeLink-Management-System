package ui;

import services.BloodRequestService;
import services.DonationHistoryService;
import services.DonorService;
import services.InventoryService;
import services.interfaces.IBloodRequestService;
import services.interfaces.IDonationHistoryService;
import services.interfaces.IDonorService;
import services.interfaces.IInventoryService;
import utils.DatabaseConnection;
import utils.interfaces.IConnectionProvider;

import java.sql.SQLException;

class ServiceInitializer {
    public ServiceContainer initializeServices() throws SQLException {
        IConnectionProvider dbConnection = new DatabaseConnection();
        IDonorService donorService = new DonorService(dbConnection);
        IInventoryService inventoryService = new InventoryService(dbConnection);
        IBloodRequestService bloodRequestService = new BloodRequestService(dbConnection);
        IDonationHistoryService donationHistoryService = new DonationHistoryService(dbConnection);  // Fixed variable name

        return new ServiceContainer(
            dbConnection,
            donorService,
            inventoryService,
            bloodRequestService,
            donationHistoryService
        );
    }
}