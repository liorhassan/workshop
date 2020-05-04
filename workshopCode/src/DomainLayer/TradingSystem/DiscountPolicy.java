package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;

import java.util.HashMap;

public class DiscountPolicy {

    private HashMap<Product, Double> discountPerProduct;

    public DiscountPolicy(){
        this.discountPerProduct = new HashMap<>();
    }
    public void addDiscount(Product p, Double percentage) {
        discountPerProduct.put(p, (percentage/100));
    }

    public double calcProductDiscount(Basket b){
        double totalDisc = 0;
        for(ProductItem pi: b.getProductItems()){
            Product p = pi.getProduct();
            int amount = pi.getAmount();
            if(this.discountPerProduct.containsKey(p)){
                totalDisc += (amount * (p.getPrice() * this.discountPerProduct.get(p)));
            }
        }
        return totalDisc;
    }
}

