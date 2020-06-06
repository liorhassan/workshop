package DomainLayer.TradingSystem.Models;

import DomainLayer.TradingSystem.StoreManaging;
import DomainLayer.TradingSystem.StoreOwning;
import DomainLayer.TradingSystem.UserPurchaseHistory;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class User {

    private ConcurrentHashMap<Store, StoreManaging> storeManagements;
    private ConcurrentHashMap<Store, StoreOwning> storeOwnings;
    private ShoppingCart shoppingCart;
    private UserPurchaseHistory purchaseHistory;
    private String username;

    public User(){
        shoppingCart = new ShoppingCart(this);
        storeManagements = new ConcurrentHashMap<>();
        storeOwnings = new ConcurrentHashMap<>();
        this.purchaseHistory = new UserPurchaseHistory(this);
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getUsername() {
        return this.username;
    }

    public UserPurchaseHistory getPurchaseHistory() {
        return this.purchaseHistory;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void removeStoreManagement(Store store) {
        storeManagements.remove(store);
    }

    public ConcurrentHashMap<Store, StoreOwning> getStoreOwnings() {
        return storeOwnings;
    }
    public ConcurrentHashMap<Store, StoreManaging> getStoreManagements() {
        return storeManagements;
    }

    public boolean hasEditPrivileges(String storeName) {
        for (Store s: storeManagements.keySet()) {
            if (s.getName().equals(storeName))
                return true;
        }
        for (Store s: storeOwnings.keySet()) {
            if (s.getName().equals(storeName))
                return true;
        }
        return false;
    }

  
    public void addManagedStore(Store store, StoreManaging storeManaging) {
        if (!storeManagements.containsKey(store))
            this.storeManagements.put(store, storeManaging);
    }

    public void addOwnedStore(Store store, StoreOwning storeOwning) {
        if (!storeOwnings.containsKey(store))
            this.storeOwnings.put(store, storeOwning);
    }

    public void emptyCart(){
        this.shoppingCart = new ShoppingCart(this);
    }

    public void addPurchaseToHistory(Purchase newPurchase) {
        this.purchaseHistory.add(newPurchase);
    }

}
