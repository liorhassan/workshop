package DomainLayer.TradingSystem.Models;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;

@Entity
@Table(name = "Inventory")
public class Inventory implements Serializable {

    private HashMap<Product,Integer> products;

    public Inventory() {
        this.products = new HashMap<>();
    }

    public HashMap<Product, Integer> getProducts() {
        return products;
    }

}
