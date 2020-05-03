package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Product;

import java.util.HashMap;

public class DiscountPolicy {

    private HashMap<Product, Double> discountPerProduct;

    public void addDiscount(Product p, Double percentage) {
        discountPerProduct.put(p, (percentage/100));
    }

    public double calcProductDiscount(Product p, int amount){
        double totalDisc = 0;
        if(this.discountPerProduct.containsKey(p)){
            totalDisc += (amount * (p.getPrice() * this.discountPerProduct.get(p)));
        }
        return totalDisc;
    }

}

