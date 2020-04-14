package ServiceLayer;

import DomainLayer.Category;
import DomainLayer.SystemHandler;

public class UpdateInventory {

    public String execute(String storeName, String productName, double productPrice, Category productCategory, String productDes, int amount){

        try {
            return SystemHandler.getInstance().updateInventory(storeName, productName, productPrice, productCategory, productDes, amount);
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }
}
