package DomainLayer;

import java.util.*;
import java.util.HashMap;

public class ShoppingCart {

    private HashMap<Store, Basket> baskets;
    private User user;

    public ShoppingCart(User user) {
        this.user = user;
        baskets = new HashMap<>();
    }

    public void addProduct(String product, Store store){
        if (!baskets.containsKey(store))
            baskets.put(store, new Basket(store));
        baskets.get(store).addProduct(store.getProductByName(product));
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection<Basket> getBaskets() {
        return baskets.values();
    }

    public String view(){
        String output = "Your ShoppingCart details: \n";
        if(baskets.isEmpty())
            return output + "empty!";
        for(Basket b : baskets.values()){
            output = output + b.viewBasket();
        }
        return output;
    }
}


