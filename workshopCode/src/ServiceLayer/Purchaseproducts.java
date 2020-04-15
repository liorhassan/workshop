package ServiceLayer;

import DomainLayer.SystemHandler;

public class Purchaseproducts {

    public String execute() {
        try {
            return SystemHandler.getInstance().purchaseProducts();
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

}
