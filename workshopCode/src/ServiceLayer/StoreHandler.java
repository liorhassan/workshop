package ServiceLayer;

import DomainLayer.TradingSystem.SystemFacade;
import DomainLayer.TradingSystem.SystemLogger;

public class StoreHandler {

    public String openNewStore(String storeName, String storeDescription){
        SystemLogger.getInstance().writeEvent(String.format("Open new store command: store name - %s, store description - %s", storeName, storeDescription));
        String[] args = {storeName, storeDescription};

        try {
            if (!SystemFacade.getInstance().checkIfActiveUserSubscribed())
                throw new RuntimeException("Only subscribed users can open a new store");
            if (SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter store name and description");
            if (SystemFacade.getInstance().storeExists(storeName))
                throw new RuntimeException("Store name already exists, please choose a different one");
            return SystemFacade.getInstance().openNewStore(storeName,storeDescription);
        }
        catch (RuntimeException e){
            SystemLogger.getInstance().writeError("Open new store error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public String addStoreOwner(String username, String storeName) {
        SystemLogger.getInstance().writeEvent(String.format("Add store owner command: new owner username - %s, store name - %s",username,storeName));
        try {
            String[] args = {username, storeName};
            if (SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter username and store name");
            if (!SystemFacade.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("This store doesn't exist");
            if(!SystemFacade.getInstance().userExists(username))
                throw new IllegalArgumentException("This username doesn't exist");
            if(!SystemFacade.getInstance().checkIfActiveUserIsOwner(storeName))
                throw new RuntimeException("You must be this store owner for this action");
            if(SystemFacade.getInstance().checkIfUserIsOwner(storeName, username))
                throw new RuntimeException("This username is already one of the store's owners");
            return SystemFacade.getInstance().appointOwner(username, storeName);
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Add store owner error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public String UpdateInventory(String storeName, String productName, double productPrice, String productCategory, String productDes, Integer amount){
        SystemLogger.getInstance().writeEvent(String.format("Update inventory command: store name - %s, product name - %s, product price - %f, product category - %s, product description - %s, amount - %d", storeName, productName, productPrice, productCategory, productDes, amount));
        try {
            String[] args = {storeName, productName};
            if (SystemFacade.getInstance().emptyString(args) || amount <= 0)
                throw new IllegalArgumentException("Must enter store name, and product info");
            if (!SystemFacade.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("This store doesn't exist");
            if (!SystemFacade.getInstance().userHasEditPrivileges(storeName))
                throw new IllegalArgumentException("Must have editing privileges");
            String[] args2 = {productDes};
            if(SystemFacade.getInstance().checkIfProductExists(storeName,productName)){
                if((amount == null && SystemFacade.getInstance().emptyString(args2)) || (amount != null && amount <0))
                    throw new IllegalArgumentException("Must enter amount bigger than 0 or product description");
            }
            else {
                if(amount == null || SystemFacade.getInstance().emptyString(args2) || amount < 0)
                    throw new IllegalArgumentException("Must enter amount bigger than 0 and product description");
            }
            return SystemFacade.getInstance().updateInventory(storeName, productName, productPrice, productCategory, productDes, amount);
        }
        catch (Exception e) {
            SystemLogger.getInstance().writeError("Update inventory error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public void resetStores(){
        SystemFacade.getInstance().resetStores();
    }

    public String addDiscount(String storeName, String productName, double percentage){
        try{
            String[] args = {storeName, productName};
            if(SystemFacade.getInstance().emptyString(args)){
                throw new IllegalArgumentException("Must enter store name and product name");
            }
            if(percentage > 100 || percentage < 0){
                throw new IllegalArgumentException("Invalid percentage value: must be between 0 and 100");
            }
            if(!SystemFacade.getInstance().storeExists(storeName)){
                throw new IllegalArgumentException("The store doesn't exist in the trading system");
            }
            if(!SystemFacade.getInstance().checkIfProductExists(storeName, productName)){
                throw new IllegalArgumentException("The product isn't available in this store");
            }
            SystemFacade.getInstance().addDiscount(storeName, productName, percentage);
            return "discount have been added to the store";
        }
        catch(Exception e){
            return e.getMessage();
        }
    }
}
