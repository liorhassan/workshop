package ServiceLayer;

import DomainLayer.SystemHandler;

public class AddToShoppingBasket {

    public String AddToShoppingBasket(String storeName, String productName){
        try {
            SystemHandler.getInstance().addToShoppingBasket(storeName, productName);
            return "Item has been added to basket";
        }
        catch (Exception e){
            return e.getMessage();
        }
    }
}
