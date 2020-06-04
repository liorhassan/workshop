package DomainLayer.TradingSystem;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.Models.Purchase;
import DomainLayer.TradingSystem.Models.Store;

import java.util.LinkedList;
import java.util.List;

public class StorePurchaseHistory {

    private List<Purchase> purchases;
    private Store store;

    public StorePurchaseHistory(Store s) {
        this.store = s;
        initPurchases();
    }

    private void initPurchases() {
        this.purchases = PersistenceController.readAllPurchases(store.getName());
    }

    public void addPurchase(Purchase p) {
        this.purchases.add(p);
    }

    public List<Purchase> getStorePurchases() {
        return this.purchases;
    }

}
