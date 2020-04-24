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
            if(!SystemHandler.getInstance().checkIfActiveUserIsOwner(storeName) || !(SystemHandler.getInstance().checkIfActiveUserIsManager(storeName)&& SystemHandler.getInstance().checkIfUserHavePermission(storeName, "View store purchase history"))){
                throw new RuntimeException("You are not allowed to view this store's purchasing history");
            }
            return getMessage(SystemHandler.getInstance().storePurchaseHistory(storeName));
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("View Store Purchase History error: " + e.getMessage());
            return e.getMessage();
        }
    }

    private String getMessage(StorePurchaseHistory purchaseHistory) {

    }


}
