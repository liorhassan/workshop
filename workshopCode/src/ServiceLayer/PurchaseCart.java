package ServiceLayer;

import DomainLayer.SystemHandler;

public class PurchaseCart {

    public String execute() {
        try {
            return SystemHandler.getInstance().purchaseCart();
        }

        catch (Exception e) {
            return e.getMessage();
        }
    }

}
