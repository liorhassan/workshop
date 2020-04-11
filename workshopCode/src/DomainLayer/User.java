package DomainLayer;

import java.util.HashMap;

public class User {

    private HashMap<Store,StoreManaging> storeManagments;

    private ShoppingCart shoppingCart;

    public User(){
        shoppingCart = new ShoppingCart(this);
        storeManagments = new HashMap<>();
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void removeStoreManagment(Store store) {
        storeManagments.remove(store);
    }
}
