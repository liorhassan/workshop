package DomainLayer;

import java.util.HashMap;

public class ShoppingCart {

    private HashMap<Store, Basket> baskets;

    public void addProduct(String product, Store store){
        if (!baskets.containsKey(store))
            baskets.put(store, new Basket());
        baskets.get(store).addProduct(store.getProductByName(product));
    }
}
