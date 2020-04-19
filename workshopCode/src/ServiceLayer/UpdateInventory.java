package ServiceLayer;

import DomainLayer.Category;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

public class UpdateInventory {

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
