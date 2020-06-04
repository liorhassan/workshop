package DomainLayer.TradingSystem.Models;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.StoreManaging;
import DomainLayer.TradingSystem.StoreOwning;
import DomainLayer.TradingSystem.UserPurchaseHistory;
import org.hibernate.usertype.UserType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Transient
    private HashMap<Store, StoreManaging> storeManagements;

    @Transient
    private HashMap<Store, StoreOwning> storeOwnings;

    @Transient
    private ShoppingCart shoppingCart;

    @Transient
    private UserPurchaseHistory purchaseHistory;

    @Id
    @Column(name = "username", unique = true)
    private String username;

    public User() {
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

    public void initCart() {
        this.shoppingCart = PersistenceController.readUserCart(this.username);
        if(this.shoppingCart == null)
            this.shoppingCart = new ShoppingCart(this);
    }

    public void removeStoreManagement(Store store) {
        storeManagements.remove(store);
    }

    public HashMap<Store, StoreOwning> getStoreOwnings() {
        return storeOwnings;
    }
    public HashMap<Store, StoreManaging> getStoreManagements() {
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
