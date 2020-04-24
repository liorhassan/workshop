package ServiceLayer;

import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

public class ShoppingCartHandler {

    public String viewCart (){
        return SystemHandler.getInstance().viewSoppingCart();
    }

    public String editCart (String storeName, String productName, int amount){
        try {
            String output = SystemHandler.getInstance().editShoppingCart(storeName, productName, amount);
            return output;
        }
        catch (Exception e){
            return e.getMessage();
        }
    }

    public String AddToShoppingBasket(String storeName, String productName, int amount){
        SystemLogger.getInstance().writeEvent("Add to shopping basket command: store name - " + storeName + ", product name - " + productName);
        try {
            SystemHandler.getInstance().addToShoppingBasket(storeName, productName, amount);
            return "Items have been added to basket";
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Add to shopping basket error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public String purchaseCart() {
        try {
            SystemLogger.getInstance().writeEvent("Purchase Cart command");
            return SystemHandler.getInstance().purchaseCart();
        }

        catch (Exception e) {
            SystemLogger.getInstance().writeError("Purchase Cart error: " + e.getMessage());
            return e.getMessage();
        }
    }
}
