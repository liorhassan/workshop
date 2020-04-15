package ServiceLayer;

import DomainLayer.SystemHandler;

public class PurchaseProducts {

    public String execute() {
        try {
            return SystemHandler.getInstance().purchaseProducts();
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

}
