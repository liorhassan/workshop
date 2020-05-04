package DomainLayer.TradingSystem.Models;

import DomainLayer.TradingSystem.ProductItem;

import java.util.*;
import java.util.HashMap;

public class ShoppingCart {

    private HashMap<Store, Basket> baskets;
    private User user;
    private double cartTotalPrice;

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
        List<ProductItem> items = basket.getProductItems();
        for(ProductItem pi : items){
            if(pi.getProduct().getName().equals(product)) {
                if (amount == 0) {
                    items.remove(pi);
                    if (items.isEmpty())
                        baskets.remove(store);
                    return "The product has been updated successfully";
                }
                else  {
                    pi.setAmount(amount);
                    return "The product has been updated successfully";
                }
            }
        }
        return "The product doesn’t exist in your shopping cart";
    }

    public boolean isProductInCart(String product, Store store){
        Basket basket = baskets.get(store);
        List<ProductItem> items = basket.getProductItems();
        for(ProductItem pi : items){
            if(pi.getProduct().getName().equals(product))
                return true;
        }
        return false;
    }

    public boolean isBasketExists (Store store){
        return baskets.containsKey(store) ;
    }

    public String viewOnlyProducts() {
        if (baskets.isEmpty())
            throw new RuntimeException("There are no products to view");
        return view().substring(28);
    }

    public void computeCartPrice() {
        double totalPrice = 0;
        for (Basket currBasket : this.baskets.values()) {
            Store currStore = currBasket.getStore();
            totalPrice += currStore.calculateTotalCheck(currBasket);
        }
        this.cartTotalPrice = totalPrice;
    }


    //for each basket in the cart - reserved the products in the basket
    //if a product is unavailable - return all reserved products in the cart and throws exception
    public void reserveBaskets(){
        for(Basket b: baskets.values()){
            Store s = b.getStore();
            ProductItem pi = s.reserveBasket(b);
            if(pi != null){
                unreserveProducts();
                throw new RuntimeException("There is currently no stock of " + pi.getAmount() + " " + pi.getProduct().getName() + " products");
            }

        }
    }

    //checks for each basket in the cart if there are reserved products in the store
    //if such products exist, it returns them
    public void unreserveProducts(){
        for(Basket b: baskets.values()){
            Store s = b.getStore();
            if(!s.getReservedProducts(b).isEmpty()){
                //there are reserved products in the basket that needs to be returned
                s.unreserveBasket(b);
            }
        }
    }

    //for each store in the cart adds it store purchase history
    public void addStoresPurchaseHistory(){
        for(Store s: baskets.keySet()){
            s.addStorePurchaseHistory(baskets.get(s), user);
        }
    }

    public boolean isEmpty(){
        return this.baskets.isEmpty();
    }

    public void addBasket(Basket basket){
        this.baskets.put(basket.getStore(), basket);
    }

    public Basket getStoreBasket(Store s){
       return baskets.get(s);
    }

    public double getTotalCartPrice(){
        return this.cartTotalPrice;
    }


}

