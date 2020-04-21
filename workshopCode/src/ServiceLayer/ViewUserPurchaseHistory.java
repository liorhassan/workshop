package ServiceLayer;

import DomainLayer.Purchase;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;
import DomainLayer.UserPurchaseHistory;


public class ViewUserPurchaseHistory {

    public String execute() {
        SystemLogger.getInstance().writeEvent("View User Purchase History command");
        try {
            return getMessage(SystemHandler.getInstance().getUserPurchaseHistory());
        } catch (RuntimeException e) {
            SystemLogger.getInstance().writeError("Register error: " + e.getMessage());
            return e.getMessage();
        }
    }

    private String getMessage(UserPurchaseHistory purchaseHistory) {
        String historyOutput = "Shopping history:" + "\n";
        int counter = 1;
        for (Purchase p : purchaseHistory.getUserPurchases()) {
            historyOutput = historyOutput + "\n" + "Purchase #" + counter + ":" + "\n";
            historyOutput = historyOutput + p.getPurchasedProducts().viewOnlyProducts();
            historyOutput = historyOutput + "\n" + "total money paid: " + p.getTotalCheck();
            counter++;
        }
        return historyOutput;
    }
}
