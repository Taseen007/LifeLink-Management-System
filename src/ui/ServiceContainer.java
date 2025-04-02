package ui;

import services.interfaces.IDonorService;
import services.interfaces.IInventoryService;
import utils.interfaces.IConnectionProvider;

class ServiceContainer {
    private final IConnectionProvider dbConnection;
    private final IDonorService donorService;
    private final IInventoryService inventoryService;

    public ServiceContainer(IConnectionProvider dbConnection,
                            IDonorService donorService,
                            IInventoryService inventoryService) {
        this.dbConnection = dbConnection;
        this.donorService = donorService;
        this.inventoryService = inventoryService;
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
}