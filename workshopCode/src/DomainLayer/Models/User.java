package DomainLayer.Models;

import DomainLayer.StoreManaging;
import DomainLayer.StoreOwning;
import DomainLayer.UserPurchaseHistory;

import java.util.HashMap;

public class User {

    private HashMap<Store, StoreManaging> storeManagements;
    private HashMap<Store, StoreOwning> storeOwnings;
    private ShoppingCart shoppingCart;
    private UserPurchaseHistory purchaseHistory;
    private String username;

    public User(){
        shoppingCart = new ShoppingCart(this);
        storeManagements = new HashMap<>();
        storeOwnings = new HashMap<>();
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

    public HashMap<Store, StoreOwning> getStoreOwnings() {
        return storeOwnings;
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
}
