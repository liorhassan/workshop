package DomainLayer;

import java.util.*;
import java.util.HashMap;

public class ShoppingCart {

    private HashMap<Store, Basket> baskets;
    private User user;
    private List<Basket> basketsList;

    public ShoppingCart(User user) {
        this.user = user;
        basketsList = new ArrayList<Basket>();
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

    public List<Basket> getBaskets() {
        return basketsList;
    }

    public String view(){
        String output = "Your ShoppingCart details: \n";
        if(baskets.isEmpty())
            return output + "empty!";
        for(Basket b : basketsList){
            output = output + b.viewBasket();
        }
        return output;
    }
}


