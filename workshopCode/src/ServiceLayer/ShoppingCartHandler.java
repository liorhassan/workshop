package ServiceLayer;

import DomainLayer.TradingSystem.SystemFacade;
import DomainLayer.TradingSystem.SystemLogger;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.UUID;

public class ShoppingCartHandler {

    public String viewCart (UUID session_id){
        return SystemFacade.getInstance().viewSoppingCart(session_id);
    }

    public String editCart (UUID session_id, String storeName, String productName, int amount){
        SystemLogger.getInstance().writeEvent("Edit shopping cart command: store name - " + storeName + ", product name - " + productName + "amount - " + amount);
        try {
            String [] args = {storeName, productName};
            if(SystemFacade.getInstance().emptyString(args)|| amount < 0 )
                throw new IllegalArgumentException("Must enter store name and product name and amount bigger than 0");
            if(! SystemFacade.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("This store doesn't exist");
            if(! SystemFacade.getInstance().checkIfBasketExists(session_id, storeName))
                throw new IllegalArgumentException("This store doesn't exist");
           if(! SystemFacade.getInstance().checkIfProductInCart(session_id, storeName, productName))
                throw new IllegalArgumentException("The product doesn’t exist in your shopping cart");
            if (!SystemFacade.getInstance().isProductAvailable(storeName, productName, amount))
                throw new IllegalArgumentException("The product isn't available in the store with the requested amount");
            return SystemFacade.getInstance().editShoppingCart(session_id, storeName, productName, amount);
        }
        catch (Exception e){

            SystemLogger.getInstance().writeError("Edit shopping cart error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }

    public String AddToShoppingBasket(UUID session_id, String storeName, String productName, int amount){
        SystemLogger.getInstance().writeEvent("Add to shopping basket command: store name - " + storeName + ", product name - " + productName);
        try {
            String[] args = {storeName, productName};
            if (SystemFacade.getInstance().emptyString(args) || amount <= 0)
                throw new IllegalArgumentException("Must enter store name and product name and amount bigger than 0");
            if (!SystemFacade.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("The store doesn't exist in the trading system");
            if (!SystemFacade.getInstance().isProductAvailable(storeName, productName, amount))
                throw new IllegalArgumentException("The product isn't available in the store with the requested amount");
            SystemFacade.getInstance().addToShoppingBasket(session_id, storeName, productName, amount);
            return createJSONMsg("SUCCESS", "Items have been added to basket");
            //return "Items have been added to basket";
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Add to shopping basket error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }

    public String purchaseCart(UUID session_id) throws SQLException {
        SystemLogger.getInstance().writeEvent("Purchase Cart command");
        try {
            if(SystemFacade.getInstance().cartIsEmpty(session_id)){
                throw new RuntimeException("The shopping cart is empty");
            }
            SystemFacade.getInstance().reserveProducts(session_id);
            SystemFacade.getInstance().computePrice(session_id);
            if (!SystemFacade.getInstance().payment(session_id)) {
                throw new RuntimeException("Payment failed");
            }
            if(!SystemFacade.getInstance().supply(session_id)){
                throw new RuntimeException("supplement failed");
            }

            SystemFacade.getInstance().addPurchaseToHistory(session_id);
            return createJSONMsg("SUCCESS", "Purchasing completed successfully");

//            return "Purchasing completed successfully";
        }
        catch (Exception e) {
//            SystemFacade.getInstance().emptyCart(session_id);
            SystemLogger.getInstance().writeError("Purchase Cart error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }


    public String getCartTotalPrice(UUID session_id){
        return SystemFacade.getInstance().getCartTotalPrice(session_id);
    }


    public String createJSONMsg(String type, String content) {
        JSONObject response = new JSONObject();
        response.put(type, content);
        return response.toJSONString();
    }
}
