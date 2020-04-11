package DomainLayer;

import java.util.*;
import java.util.HashMap;

public class ShoppingCart {

    private HashMap<Store, Basket> baskets;
    private User user;

    public ShoppingCart(User user) {
        this.user = user;
        this.baskets = new HashMap<>();
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

    public String view(){
        String output = "Your ShoppingCart details: \n";
        if(baskets.isEmpty())
            return output + "empty!";
        for(Basket b : baskets.values()){
            output = output + b.viewBasket();
        }
        return output;
    }


    public String edit(Store store, String product, int amount){
        Basket basket = baskets.get(store);
        List<ProductItem> items = basket.getProductItems();
        for(ProductItem pi : items){
            if(pi.getProduct().getName().equals(product)) {
                if (amount == 0) {
                    items.remove(pi);
                    if (items.isEmpty())
                        baskets.remove(store);
                    return "edited!";
                } else {
                    pi.setAmount(amount);
                    return "edited!";
                }
            }
        }
        return "The product doesn’t exist in your shopping cart";
    }
}


