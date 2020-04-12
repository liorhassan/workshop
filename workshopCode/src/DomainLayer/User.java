package DomainLayer;

import java.util.HashMap;

public class User {

    private HashMap<Store,StoreManaging> storeManagments;
    private ShoppingCart shoppingCart;
    private UserPurchaseHistory purchaseHistory;
    private String username;

    public User(String name){
        this.username = name;
        shoppingCart = new ShoppingCart(this);
        storeManagments = new HashMap<>();
        this.purchaseHistory = new UserPurchaseHistory(this);
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
}
