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
            String[] args = {storeName, productName};
            if (SystemHandler.getInstance().emptyString(args) || amount <= 0)
                throw new IllegalArgumentException("Must enter store name and product name and amount bigger than 0");
            if (!SystemHandler.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("The store doesn't exist in the trading system");
            if (!SystemHandler.getInstance().isProductAvailable(storeName, productName, amount))
                throw new IllegalArgumentException("The product isn't available in the store with the requested amount");
            SystemHandler.getInstance().addToShoppingBasket(storeName, productName, amount);
            return "Items have been added to basket";
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Add to shopping basket error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public String purchaseCart() {
        SystemLogger.getInstance().writeEvent("Purchase Cart command");
        try {
            if(SystemHandler.getInstance().cartIsEmpty()){
                throw new RuntimeException("The shopping cart is empty");
            }
            return SystemHandler.getInstance().purchaseCart();
        }

        catch (Exception e) {
            SystemLogger.getInstance().writeError("Purchase Cart error: " + e.getMessage());
            return e.getMessage();
        }
    }
}
