package DomainLayer;

import java.util.HashMap;

public class User {

    private HashMap<Store,StoreManaging> storeManagments;
    private HashMap<Store, StoreOwning> storeOwnings;
    private ShoppingCart shoppingCart;
    private UserPurchaseHistory purchaseHistory;
    private String username;

    public User(){
        shoppingCart = new ShoppingCart(this);
        storeManagments = new HashMap<>();
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

    public void removeStoreManagment(Store store) {
        storeManagments.remove(store);
    }

    public boolean hasEditPrivileges(String storeName) {
        //TODO: implement
        return true;
    }

    public void addManagedStore(Store store, StoreManaging storeManaging) {
        this.storeManagments.put(store, storeManaging);
    }

    public void addOwnedStore(Store store, StoreOwning storeOwning) {
        this.storeOwnings.put(store, storeOwning);
    }
}
