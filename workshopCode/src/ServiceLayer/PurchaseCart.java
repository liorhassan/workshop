package ServiceLayer;

import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

public class PurchaseCart {

    public String execute() {
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
