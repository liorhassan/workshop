package ServiceLayer;

import DomainLayer.Models.Purchase;
import DomainLayer.StorePurchaseHistory;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;
import DomainLayer.UserPurchaseHistory;

public class ViewPurchaseHistoryHandler {

    // UseCase 6.4 - Admin only
    public String viewPurchaseHistoryOfUserAsAdmin(String username) {
        SystemLogger.getInstance().writeEvent("View user purchase history command as an admin");
        try {
            String[] args = {username};
            if (SystemHandler.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter username");
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
    public String viewPurchaseHistoryOfStoreAsAdmin (String storeName) {
        SystemLogger.getInstance().writeEvent("View store purchase history command as an admin");
        try {
            String[] args = {storeName};
            if (SystemHandler.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter store name");
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
            String[] arg = {storeName};
            if(SystemHandler.getInstance().emptyString(arg)){
                throw new RuntimeException("Must enter store name");
            }
            if(!SystemHandler.getInstance().storeExists(storeName)){
                throw new RuntimeException("This store doesn't exist");
            }
            if(!(SystemHandler.getInstance().checkIfActiveUserIsOwner(storeName) || (SystemHandler.getInstance().checkIfActiveUserIsManager(storeName)&& SystemHandler.getInstance().checkIfUserHavePermission(storeName, "View store purchase history")))){
                throw new RuntimeException("You are not allowed to view this store's purchasing history");
            }
            return SystemHandler.getInstance().getStorePurchaseHistory(storeName);
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("View Store Purchase History error: " + e.getMessage());
            return e.getMessage();
        }
    }
}
