package ServiceLayer;

import DomainLayer.Models.Purchase;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;
import DomainLayer.UserPurchaseHistory;

public class ViewPurchaseHistoryHandler {

    public String ViewPurchaseHistoryOfUser() {

        SystemLogger.getInstance().writeEvent("View User Purchase History command");
        try {
            if (SystemHandler.getInstance().checkIfActiveUserSubscribed())
                throw new RuntimeException("Only subscribed users can view purchase history");
            return SystemHandler.getInstance().getActiveUserPurchaseHistory();
        }
        catch (RuntimeException e) {
            SystemLogger.getInstance().writeError("View User Purchase History error: " + e.getMessage());
            return e.getMessage();
        }
    }
}
