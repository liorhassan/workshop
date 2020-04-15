package DomainLayer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class User {

    private HashMap<Store,StoreManaging> storeManagments;
    private ShoppingCart shoppingCart;
    private UserPurchaseHistory purchaseHistory;
    private String username;
    private List<Permission> permission;

    public User(){
        shoppingCart = new ShoppingCart(this);
        storeManagments = new HashMap<>();
        this.purchaseHistory = new UserPurchaseHistory(this);
        this.permission = new LinkedList<>();
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

    public List<Permission> getPermission(){
        return  permission;
    }

    public void setPermission(List<Permission> p){
        permission = p;
    }
}
