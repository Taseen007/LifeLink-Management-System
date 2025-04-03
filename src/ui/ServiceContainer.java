package ui;

import services.interfaces.IBloodRequestService;
import services.interfaces.IDonorService;
import services.interfaces.IInventoryService;
import services.interfaces.IDonationHistoryService;  // Add this import
import utils.interfaces.IConnectionProvider;

class ServiceContainer {
    private final IConnectionProvider dbConnection;
    private final IDonorService donorService;
    private final IInventoryService inventoryService;
    private final IBloodRequestService bloodRequestService;
    private final IDonationHistoryService donationHistoryService;  // Add this field

    public ServiceContainer(IConnectionProvider dbConnection,
                          IDonorService donorService,
                          IInventoryService inventoryService,
                          IBloodRequestService bloodRequestService,
                          IDonationHistoryService donationHistoryService) {  // Add parameter
        this.dbConnection = dbConnection;
        this.donorService = donorService;
        this.inventoryService = inventoryService;
        this.bloodRequestService = bloodRequestService;
        this.donationHistoryService = donationHistoryService;  // Initialize new field
    }

    public IConnectionProvider getDbConnection() {
        return dbConnection;
    }

    public IDonorService getDonorService() {
        return donorService;
    }

    public IInventoryService getInventoryService() {
        return inventoryService;
    }

    public IBloodRequestService getBloodRequestService() {  // Add getter
        return bloodRequestService;
    }

    public IDonationHistoryService getDonationHistoryService() {  // Add new getter
        return donationHistoryService;
    }
}