package ServiceLayer;

import DomainLayer.Security.SecurityHandler;
import DomainLayer.SystemHandler;

public class ViewAndEditShoppingCart {

    public String view (){

        return SystemHandler.getInstance().viewSoppingCart();
    }

    public String edit (String storeName, String productName, int amount){
        try {

           String output = SystemHandler.getInstance().editShoppingCart(storeName, productName, amount);

            return output;
        }
        catch (Exception e){
            return e.getMessage();
        }
    }
}
