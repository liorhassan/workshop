package ServiceLayer;

import DomainLayer.*;
import DomainLayer.Models.Product;
import DomainLayer.Models.Store;

import java.util.Collection;


public class ViewInfoHandler {

    public String viewStoreinfo(String storeName) {
        SystemLogger.getInstance().writeEvent("View Store Info command");
        try {
            String[] arg = {storeName};
            if(SystemHandler.getInstance().emptyString(arg)) {
                throw new IllegalArgumentException("The store name is invalid");
            }
            if(!SystemHandler.getInstance().storeExists(storeName)){
                throw new RuntimeException("This store doesn't exist in this trading system");
            }
            return SystemHandler.getInstance().viewStoreInfo(storeName);

        } catch (Exception e) {
            SystemLogger.getInstance().writeError("View Store Info error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public String viewProductInfo(String storeName, String productName) {
        SystemLogger.getInstance().writeEvent("View Product Info command");
        try {
            String[] args = {storeName, productName};
            if(SystemHandler.getInstance().emptyString(args)){
                throw new IllegalArgumentException("The product name is invalid");
            }
            if(!SystemHandler.getInstance().checkIfProductExists(storeName, productName)){
                throw new RuntimeException("This product is not available for purchasing in this store");
            }

            return SystemHandler.getInstance().viewProductInfo(storeName, productName);
        } catch (Exception e) {
            SystemLogger.getInstance().writeError("View Product Info error: " + e.getMessage());
            return e.getMessage();
        }
    }
}
