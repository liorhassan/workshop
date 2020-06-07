package ServiceLayer;

import DomainLayer.TradingSystem.SystemFacade;
import DomainLayer.TradingSystem.SystemLogger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.UUID;

public class ViewPurchaseHistoryHandler {

    // UseCase 6.4 - Admin only
    public String viewPurchaseHistoryOfUserAsAdmin(UUID session_id, String username) {
        SystemLogger.getInstance().writeEvent("View user purchase history command as an admin");
        try {
            String[] args = {username};
            if (SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter username");
            if (!SystemFacade.getInstance().checkIfInAdminMode(session_id))
                throw new RuntimeException("Only admin user can view other users' purchase history");
            if (!SystemFacade.getInstance().userExists(username))
                throw new IllegalArgumentException("The user requested doesn't exist in the system");
            return SystemFacade.getInstance().getUserPurchaseHistory(username);
        }
        catch (RuntimeException e) {
            SystemLogger.getInstance().writeError("View user purchase history as admin error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return e.getMessage();
        }
    }

    // UseCase 6.4 - Admin only
    public String viewPurchaseHistoryOfStoreAsAdmin (UUID session_id, String storeName) {
        SystemLogger.getInstance().writeEvent("View store purchase history command as an admin");
        try {
            String[] args = {storeName};
            if (SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter store name");
            if (!SystemFacade.getInstance().checkIfInAdminMode(session_id))
                throw new RuntimeException("Only admin user can view store's purchase history");
            if (!SystemFacade.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("The store requested doesn't exist in the system");
            return SystemFacade.getInstance().getStorePurchaseHistory(storeName);
        }
        catch (RuntimeException e) {
            SystemLogger.getInstance().writeError("View store purchase history as admin error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }

    // UseCase 3.7
    public String viewLoggedInUserPurchaseHistory(UUID session_id) {

        SystemLogger.getInstance().writeEvent("View User Purchase History command");
        try {
            if (!SystemFacade.getInstance().checkIfActiveUserSubscribed(session_id))
                throw new RuntimeException("Only subscribed users can view purchase history");
            return SystemFacade.getInstance().getActiveUserPurchaseHistory(session_id);
        }
        catch (RuntimeException e) {
            SystemLogger.getInstance().writeError("View User Purchase History error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return e.getMessage();
        }
    }

    public String ViewPurchaseHistoryOfStore(UUID session_id, String storeName){
        SystemLogger.getInstance().writeEvent("View Store Purchase History command: store name - " + storeName);
        try{
            String[] arg = {storeName};
            if(SystemFacade.getInstance().emptyString(arg)){
                throw new RuntimeException("Must enter store name");
            }
            if(!SystemFacade.getInstance().storeExists(storeName)){
                throw new RuntimeException("This store doesn't exist");
            }
            if(!(SystemFacade.getInstance().checkIfActiveUserIsOwner(session_id, storeName) || (SystemFacade.getInstance().checkIfActiveUserIsManager(session_id, storeName)&& SystemFacade.getInstance().checkIfUserHavePermission(session_id, storeName, "View Purchasing History")))){
                throw new RuntimeException("You are not allowed to view this store's purchasing history");
            }
            return SystemFacade.getInstance().getStorePurchaseHistory(storeName);
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("View Store Purchase History error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }
}
