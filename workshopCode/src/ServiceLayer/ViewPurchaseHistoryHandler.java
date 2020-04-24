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
