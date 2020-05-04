package ServiceLayer;

import DomainLayer.TradingSystem.SystemFacade;
import DomainLayer.TradingSystem.SystemLogger;

public class ShoppingCartHandler {

    public String viewCart (){
        return SystemFacade.getInstance().viewSoppingCart();
    }

    public String editCart (String storeName, String productName, int amount){
        SystemLogger.getInstance().writeEvent("Edit shopping cart command: store name - " + storeName + ", product name - " + productName + "amount - " + amount);
        try {
            String [] args = {storeName, productName};
            if(SystemFacade.getInstance().emptyString(args)|| amount < 0 )
                throw new IllegalArgumentException("Must enter store name and product name and amount bigger than 0");
            if(! SystemFacade.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("This store doesn't exist");
            if(! SystemFacade.getInstance().checkIfBasketExists(storeName))
                throw new IllegalArgumentException("This store doesn't exist");
           if(! SystemFacade.getInstance().checkIfProductInCart(storeName, productName))
                throw new IllegalArgumentException("The product doesn’t exist in your shopping cart");
            if (!SystemFacade.getInstance().isProductAvailable(storeName, productName, amount))
                throw new IllegalArgumentException("The product isn't available in the store with the requested amount");
            String output = SystemFacade.getInstance().editShoppingCart(storeName, productName, amount);
            return output;
        }
        catch (Exception e){

            SystemLogger.getInstance().writeError("Edit shopping cart error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public String AddToShoppingBasket(String storeName, String productName, int amount){
        SystemLogger.getInstance().writeEvent("Add to shopping basket command: store name - " + storeName + ", product name - " + productName);
        try {
            String[] args = {storeName, productName};
            if (SystemFacade.getInstance().emptyString(args) || amount <= 0)
                throw new IllegalArgumentException("Must enter store name and product name and amount bigger than 0");
            if (!SystemFacade.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("The store doesn't exist in the trading system");
            if (!SystemFacade.getInstance().isProductAvailable(storeName, productName, amount))
                throw new IllegalArgumentException("The product isn't available in the store with the requested amount");
            SystemFacade.getInstance().addToShoppingBasket(storeName, productName, amount);
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
            if(SystemFacade.getInstance().cartIsEmpty()){
                throw new RuntimeException("The shopping cart is empty");
            }
            SystemFacade.getInstance().reserveProducts();
            SystemFacade.getInstance().computePrice();
            if (!SystemFacade.getInstance().payment()) {
                throw new RuntimeException("Payment failed");
            }
            if(!SystemFacade.getInstance().supply()){
                throw new RuntimeException("supplement failed");
            }

            SystemFacade.getInstance().addPurchaseToHistory();
            return "Purchasing completed successfully";
        }

        catch (Exception e) {
            SystemFacade.getInstance().emptyCart();
            SystemLogger.getInstance().writeError("Purchase Cart error: " + e.getMessage());
            return e.getMessage();
        }
    }
}
