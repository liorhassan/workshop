package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Purchase;
import DomainLayer.TradingSystem.Models.User;

import java.util.LinkedList;
import java.util.List;

public class UserPurchaseHistory {

    private List<Purchase> purchases;
    private User purchasingUser;

    public UserPurchaseHistory(User user) {
        this.purchasingUser = user;
        this.purchases = new LinkedList<>();
    }

    public List<Purchase> getUserPurchases() {
        return this.purchases;
    }

    public void add(Purchase newPurchase) {
        this.purchases.add(newPurchase);
    }
}
