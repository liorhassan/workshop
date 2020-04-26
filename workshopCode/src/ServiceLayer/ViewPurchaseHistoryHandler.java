package ServiceLayer;

import DomainLayer.Models.Purchase;
import DomainLayer.StorePurchaseHistory;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;
import DomainLayer.UserPurchaseHistory;

public class ViewPurchaseHistoryHandler {

    // UseCase 6.4 - Admin only
    public String viewPurchaseHistoryOfUser(String username) {
        SystemLogger.getInstance().writeEvent("View User Purchase History command as an Admin");
        try {
            if (SystemHandler.getInstance().checkIfInAdminMode())
                throw new RuntimeException("Only admin user can view other users' purchase history");
            if (SystemHandler.getInstance().userExists(username))
                throw new IllegalArgumentException("The user requested doesn't exist in the system");
            return SystemHandler.getInstance().getUserPurchaseHistory(username);
        }
        catch (RuntimeException e) {
            SystemLogger.getInstance().writeError("View user purchase history as admin error: " + e.getMessage());
            return e.getMessage();
        }
    }

    // UseCase 6.4 - Admin only
    public String viewPurchaseHistoryOfStore (String storeName) {
        SystemLogger.getInstance().writeEvent("View User Purchase History command as an Admin");
        try {
            if (SystemHandler.getInstance().checkIfInAdminMode())
                throw new RuntimeException("Only admin user can view store's purchase history");
            if (SystemHandler.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("The store requested doesn't exist in the system");
            return SystemHandler.getInstance().getStorePurchaseHistory(storeName);
        }
        catch (RuntimeException e) {
            SystemLogger.getInstance().writeError("View store purchase history as admin error: " + e.getMessage());
            return e.getMessage();
        }
    }

    // UseCase 3.7
    public String viewLoggedInUserPurchaseHistory() {

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

    public String ViewPurchaseHistoryOfStore(String storeName){
        SystemLogger.getInstance().writeEvent("View Store Purchase History command: store name - " + storeName);
        try{

            return getMessage(SystemHandler.getInstance().storePurchaseHistory(storeName));
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("View Store Purchase History error: " + e.getMessage());
            return e.getMessage();
        }
    }

    private String getMessage(StorePurchaseHistory purchaseHistory) {
        String historyOutput = "Shopping history:";
        int counter = 1;
        for (Purchase p : purchaseHistory.getPurchases()) {
            historyOutput = historyOutput.concat("\n" + "Purchase #" + counter + ":" + "\n");
            historyOutput = historyOutput.concat(p.getPurchasedProducts().viewStoreHistoryBasket());
            historyOutput = historyOutput.concat("\n" + "total money paid: " + p.getTotalCheck());
            counter++;
        }
        return historyOutput;
    }
}
