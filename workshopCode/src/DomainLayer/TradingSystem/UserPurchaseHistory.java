package DomainLayer.TradingSystem;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.Models.Purchase;
import DomainLayer.TradingSystem.Models.User;

import java.sql.SQLException;
import java.util.List;

public class UserPurchaseHistory {

    private List<Purchase> purchases;
    private User purchasingUser;

    public UserPurchaseHistory(User user) throws SQLException {
        this.purchasingUser = user;
        initPurchases();
    }

    private void initPurchases() throws SQLException {
        this.purchases = PersistenceController.readAllPurchases(purchasingUser.getUsername());
    }

    public List<Purchase> getUserPurchases() {
        return this.purchases;
    }

    public void add(Purchase newPurchase) {
        this.purchases.add(newPurchase);
    }
}
