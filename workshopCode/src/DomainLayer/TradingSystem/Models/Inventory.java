package DomainLayer.TradingSystem.Models;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.ProductItem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

public class Inventory implements Serializable {

    private ConcurrentHashMap<Product,Integer> products;

    private ConcurrentHashMap<Basket, List<ProductItem>> reservedProducts;

    public Inventory() {
        this.products = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<Product, Integer> getProducts() {
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
    public boolean reserveProduct(ProductItem pi, Basket b){
        Product p = pi.getProduct();
        int amount = pi.getAmount();
        if(checkIfProductAvailable(p.getName(), amount)){
            int prevAmount = this.products.get(p);
            if(prevAmount == amount){
                this.products.remove(p);
                p.setQuantity(0);
                if(!reservedProducts.containsKey(b)){
                    List<ProductItem> pis = new LinkedList<>();
                    reservedProducts.put(b, pis);

                }
                reservedProducts.get(b).add(pi);

                // update database
                PersistenceController.update(p);
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
            PersistenceController.create(p);
        }
        else{
            products.put(p, amount);
            p.setQuantity(amount);
            PersistenceController.update(p);
        }
    }

    public void unreserveBasket(Basket b) {
        for (ProductItem pi : this.reservedProducts.get(b)) {
            unreserveProduct(pi.getProduct(), pi.getAmount());
            PersistenceController.create(pi);
        }
    }

    public void init(String storeName) {

        List<Product> storeProducts = PersistenceController.readAllProducts(storeName,true);
        for(int i = 0; i < storeProducts.size(); i++) {
            Product p = storeProducts.get(i);
            this.products.put(p, p.getQuantity());
        }
    }


    public List<ProductItem> getReservedProducts(Basket b){
        return this.reservedProducts.get(b);
    }
}
