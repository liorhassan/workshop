package DomainLayer.TradingSystem.Models;

import DomainLayer.TradingSystem.ProductItem;

import java.util.Collection;
import java.util.HashMap;

public class Inventory {
    private HashMap<Product,Integer> products;

    public Inventory() {
        this.products = new HashMap<>();
    }

    public HashMap<Product, Integer> getProducts() {
        return products;
    }

    public void checkProductsAvailabilityInInventory(Basket b){
        Collection<ProductItem> productsItems = b.getProductItems();
        for (ProductItem pi : productsItems) {
            Product p = pi.getProduct();
            int amount = pi.getAmount();
            if (!((products.get(p) != null) && (products.get(p) - amount >= 0))){
                throw new RuntimeException("There is currently no stock of " + amount + " " + p.getName() + " products");
            }
        }
    }
}
