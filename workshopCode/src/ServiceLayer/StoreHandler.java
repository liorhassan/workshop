package ServiceLayer;

import DomainLayer.Category;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

public class StoreHandler {

    public String openNewStore(String storeName, String storeDescription){
        SystemLogger.getInstance().writeEvent(String.format("Open new store command: store name - %s, store description - %s", storeName, storeDescription));
        String[] args = {storeName, storeDescription};

        try {
            if (SystemHandler.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter store name and description");
            if (SystemHandler.getInstance().checkIfStoreExists(storeName))
                throw new RuntimeException("Store name already exists, please choose a different one");
            return SystemHandler.getInstance().openNewStore(storeName,storeDescription);
        }
        catch (RuntimeException e){
            SystemLogger.getInstance().writeError("Open new store error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public String addStoreOwner(String username, String storeName) {
        SystemLogger.getInstance().writeEvent(String.format("Add store owner command: new owner username - %s, store name - %s",username,storeName));
        try {
            return SystemHandler.getInstance().appointOwner(username, storeName);
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Add store owner error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public String UpdateInventory(String storeName, String productName, double productPrice, Category productCategory, String productDes, int amount){
        SystemLogger.getInstance().writeEvent(String.format("Update inventory command: store name - %s, product name - %s, product price - %f, product category - %s, product description - %s, amount - %d", storeName, productName, productPrice, productCategory.name(), productDes, amount));
        try {
            return SystemHandler.getInstance().updateInventory(storeName, productName, productPrice, productCategory, productDes, amount);
        }
        catch (Exception e) {
            SystemLogger.getInstance().writeError("Update inventory error: " + e.getMessage());
            return e.getMessage();
        }
    }
}
