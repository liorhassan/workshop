package DomainLayer.TradingSystem.Models;

import DataAccessLayer.PersistenceController;

import java.io.Serializable;
import java.util.HashMap;

public class Inventory {

    private HashMap<Product,Integer> products;

    public Inventory() {
        this.products = new HashMap<>();
    }

    public HashMap<Product, Integer> getProducts() {
        return products;
    }

    public Product getProductByName(String productName){
        for (Product p : products.keySet()) {
            if (p.getName().equals (productName))
                return p;
        }
        return null;
    }

    public boolean checkIfProductAvailable(String product, int amount){
        Product p = getProductByName(product);
        if(p == null)
            return false;
        return products.get(p) >= amount;
    }

    //reserve a specific product
    //returns false if the product is unavailable in the inventory
    public boolean reserveProduct(Product p, int amount){
        if(checkIfProductAvailable(p.getName(), amount)){
            int prevAmount = this.products.get(p);
            if(prevAmount == amount){
                this.products.remove(p);

                // update database
                PersistenceController.delete(p);
            }
            else{
                this.products.replace(p, prevAmount - amount);
                p.setQuantity(prevAmount - amount);

                // update database
                PersistenceController.update(p);
            }
            return true;
        }
        return false;
    }

    public void unreserveProduct(Product p, int amount) {
        if(products.get(p) != null){
            int newAmount = products.get(p) + amount;
            products.put(p, newAmount);
            p.setQuantity(newAmount);
        }
        else{
            products.put(p, amount);
            p.setQuantity(amount);
        }
        PersistenceController.update(p);
    }

    public void init() {
        PersistenceController.
    }
}
