package ServiceLayer;

import DomainLayer.Purchase;
import DomainLayer.SystemHandler;
import DomainLayer.UserPurchaseHistory;


public class WatchUserPurchaseHistory {

    public String execute() {
        try {
            return getMessage(SystemHandler.getInstance().getUserPurchaseHistory());
        } catch (RuntimeException e) {
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
