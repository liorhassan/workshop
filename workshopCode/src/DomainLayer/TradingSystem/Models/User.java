package DomainLayer.TradingSystem.Models;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.StoreManaging;
import DomainLayer.TradingSystem.StoreOwning;
import DomainLayer.TradingSystem.UserPurchaseHistory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Table(name = "users")
public class User implements Serializable {
    @Transient
    private ConcurrentHashMap<Store, StoreManaging> storeManagements;

    @Transient
    private ConcurrentHashMap<Store, StoreOwning> storeOwnings;

    @Transient
    private ShoppingCart shoppingCart;

    @Transient
    private UserPurchaseHistory purchaseHistory;


    @Id
    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "isAdmin")
    private boolean isAdmin;

    public User() {
        shoppingCart = new ShoppingCart(this);
        storeManagements = new ConcurrentHashMap<>();
        storeOwnings = new ConcurrentHashMap<>();
        this.isAdmin = false;
        this.purchaseHistory = new UserPurchaseHistory(this);//TODO: INIT PURCHASES
    }

    public void setUsername(String name) {
        this.username = name;
        this.shoppingCart.setUserName(username);
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

    public void initCart() {
        this.shoppingCart = PersistenceController.readUserCart(this.username);
        shoppingCart.setUser(this);
        if(this.shoppingCart == null)
            this.shoppingCart = new ShoppingCart(this);
        else
            shoppingCart.initBaskets(shoppingCart);
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

    public boolean getIsAdmin(){
        return this.isAdmin;
    }

    public void setIsAdmin(){
        this.isAdmin = true;
    }
}
