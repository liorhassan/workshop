package ServiceLayer;

import DomainLayer.*;

import java.util.Collection;

public class ViewStorePurchaseHistory {

    public String execute(String storeName){
        try{
            SystemLogger.getInstance().writeEvent(String.format("View Store Purchase History command: store name - %s", storeName));
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
