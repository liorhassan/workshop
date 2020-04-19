package ServiceLayer;

import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

public class AddToShoppingBasket {

    public String AddToShoppingBasket(String storeName, String productName, int amount){
        SystemLogger.getInstance().writeEvent("Add to shopping basket command: store name - " + storeName + ", product name - " + productName);
        try {
            SystemHandler.getInstance().addToShoppingBasket(storeName, productName, amount);
            return "Item has been added to basket";
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Add to shopping basket error: " + e.getMessage());
            return e.getMessage();
        }
    }
}
