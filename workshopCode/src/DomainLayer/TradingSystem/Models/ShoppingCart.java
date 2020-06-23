package DomainLayer.TradingSystem.Models;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.DiscountBInterface;
import DomainLayer.TradingSystem.ProductItem;
import DomainLayer.TradingSystem.SystemFacade;
import jdk.jfr.Enabled;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Table(name = "shoppingCarts")
public class ShoppingCart implements Serializable {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue
    private int id;

    @Transient
    private ConcurrentHashMap<Store, Basket> baskets;


    @Column(name = "userName")
    private String userName;

    @Transient
    private User user;

    @Column(name = "isHistory")
    private boolean isHistory;

    @Column(name = "totalPrice")
    private double cartTotalPrice;

    @Transient
    private int paymentTransactionId;

    @Transient
    private int supplementTransactionId;

    public ShoppingCart() {
    }

    public ShoppingCart(User user) {
        this.user = user;
        this.userName = user.getUsername();
        this.baskets = new ConcurrentHashMap<Store, Basket>();
        this.isHistory = false;
    }

    public void initBaskets(ShoppingCart sc) {
        this.baskets = new ConcurrentHashMap<>();
        List<Basket> b = PersistenceController.readAllBasket(id);

        for(Basket curr: b) {
            curr.setStore(SystemFacade.getInstance().getStoreByName(curr.getStoreName()));
            curr.setCart(sc);
            curr.initProductItems(curr);
            baskets.put(curr.getStore(), curr);
        }
    }

    public void addProduct(String product, Store store, int amount, boolean persist) {
        if (!baskets.containsKey(store)) {
            Basket b = new Basket(store, this);

            //create basket in DB
            if (persist) {
                PersistenceController.create(b);
            }
            baskets.put(store, b);
        }
        baskets.get(store).addProduct(store.getProductByName(product), amount);

        //update DB
//        if (persist) {
//            PersistenceController.update(baskets.get(store));
//        }
    }

    public User getUser() {
        return user;
    }

    public boolean getIsHistory() {
        return this.isHistory;
    }

    public void setIsHistory() {
        this.isHistory = !isHistory;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection<Basket> getBaskets() {
        return baskets.values();
    }

    public String view() {
//        String output = "Your ShoppingCart details: \n";
//        JSONObject response = new JSONObject();
        JSONArray response = new JSONArray();
        if (!baskets.isEmpty()) {
            for (Basket b : baskets.values()) {
                JSONArray currBasket = b.viewBasket();
                Iterator itr = currBasket.iterator();
                while (itr.hasNext())
                    response.add(itr.next());
            }
        }
        return response.toJSONString();
    }

    public String edit(Store store, String product, int amount, boolean persist) {
        Basket basket = baskets.get(store);
        List<ProductItem> items = basket.getProductItems();
        JSONObject response = new JSONObject();
        for (ProductItem pi : items) {
            if (pi.getProduct().getName().equals(product)) {
                if (amount == 0) {
                    items.remove(pi);
                    if (items.isEmpty()) {
                        baskets.remove(store);

                        //update DB
                        if (persist) {
                            PersistenceController.delete(pi);
                            PersistenceController.delete(baskets);
                        }
                    } else {
                        if (persist) {
                            PersistenceController.delete(pi);
                        }
                    }
                    response.put("SUCCESS", "The product has been updated successfully");
                    return response.toJSONString();
                    //return "The product has been updated successfully";
                } else {
                    pi.setAmount(amount);

                    //update DB
                    if (persist) {
                        PersistenceController.update(pi);
                    }
                    response.put("SUCCESS", "The product has been updated successfully");
                    return response.toJSONString();
                    //return "The product has been updated successfully";
                }
            }
        }
        throw new RuntimeException("The product doesn’t exist in your shopping cart");
        //response.put("ERROR", "The product doesn’t exist in your shopping cart");
        //return response.toJSONString();
        //return "The product doesn’t exist in your shopping cart";
    }

    public boolean isProductInCart(String product, Store store) {
        Basket basket = baskets.get(store);
        List<ProductItem> items = basket.getProductItems();
        for (ProductItem pi : items) {
            if (pi.getProduct().getName().equals(product))
                return true;
        }
        return false;
    }

    public boolean isBasketExists(Store store) {
        return baskets.containsKey(store);
    }

    public String viewOnlyProducts() {
        if (baskets.isEmpty())
            throw new RuntimeException("There are no products to view");
        return view();
        //return view().substring(28);
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
    public void reserveBaskets() {
        for (Basket b : baskets.values()) {
            try {
                b.reserve();
            } catch (Exception e) {
                unreserveProducts();
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    //checks for each basket in the cart if there are reserved products in the store
    //if such products exist, it returns them
    public void unreserveProducts() {
        for (Basket b : baskets.values()) {
            b.unreserve();
        }
    }

    //for each store in the cart adds it store purchase history
    public void addStoresPurchaseHistory() {
        for (Store s : baskets.keySet()) {
            s.addStorePurchaseHistory(baskets.get(s), user);
        }
    }

    public boolean isEmpty() {
        return this.baskets.isEmpty();
    }

    public void addBasket(Basket basket) {
        this.baskets.put(basket.getStore(), basket);
    }

    public Basket getStoreBasket(Store s) {
        return baskets.get(s);
    }

    public double getTotalCartPrice() {
        computeCartPrice();
        return this.cartTotalPrice;
    }

    public void notifyOwners() {
        for (Store s : baskets.keySet()) {
            s.notifyOwners(baskets.get(s), this.user.getUsername());
        }
    }


    public int getId() {
        return this.id;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    public String getUserName() {
        return this.userName;
    }

    public void emptyCart() {
        this.baskets = new ConcurrentHashMap<Store, Basket>();
        this.isHistory = false;
        this.cartTotalPrice = 0;
    }

    public int getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(int id){
        this.paymentTransactionId = id;
    }

    public int getSupplementTransactionId() {
        return supplementTransactionId;
    }

    public void setSupplementTransactionId(int id){
        this.supplementTransactionId = id;
    }
}


