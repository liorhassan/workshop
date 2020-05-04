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

}
