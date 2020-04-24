package DomainLayer.Models;

import DomainLayer.ProductItem;

import java.util.*;
import java.util.HashMap;

public class ShoppingCart {

    private HashMap<Store, Basket> baskets;
    private User user;

    public ShoppingCart(User user) {
        this.user = user;
        this.baskets = new HashMap<>();
    }

    public void addProduct(String product, Store store, int amount){
        if (!baskets.containsKey(store))
            baskets.put(store, new Basket(store));
        baskets.get(store).addProduct(store.getProductByName(product), amount);
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

    public String edit(Store store, String product, int amount){
        Basket basket = baskets.get(store);
        if(basket == null)
            return  "This store doesn't exist";
        List<ProductItem> items = basket.getProductItems();
        for(ProductItem pi : items){
            if(pi.getProduct().getName().equals(product)) {
                if (amount == 0) {
                    items.remove(pi);
                    if (items.isEmpty())
                        baskets.remove(store);
                    return "The product has been updated successfully";
                }
                else if(store.checkIfProductAvailable(pi.getProduct().getName(), amount)) {
                    pi.setAmount(amount);
                    return "The product has been updated successfully";
                }
                else {
                    return "this amount is not available";
                }
            }
        }
        return "The product doesn’t exist in your shopping cart";
    }
    public String viewOnlyProducts() {
        if (baskets.isEmpty())
            throw new RuntimeException("There are no products to view");
        String result = "";
        for(Basket b : baskets.values()){
            result = result + b.viewBasket();
        }
        return result;
    }

    public String viewBasketForStoreHistory(){
        if (baskets.isEmpty())
            throw new RuntimeException("There are no products to view");
        String result = "";
        for(Basket b : baskets.values()) {
            for(ProductItem pi: b.getProductItems()) {
                result = result.concat("Product name: " + pi.getProduct().getName() + " price: " + pi.getProduct().getPrice() + " amount: " + pi.getAmount() + "\n");
            }
        }
        return result;
    }

    public boolean isEmpty(){
        return this.baskets.isEmpty();
    }

    public void addBasket(Basket basket){
        this.baskets.put(basket.getStore(), basket);
    }
}


