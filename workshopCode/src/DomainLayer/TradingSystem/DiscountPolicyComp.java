package DomainLayer.TradingSystem;
import java.util.ArrayList;
import java.util.List;
import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;

import java.util.HashMap;

public class DiscountPolicyComp {

    //private HashMap<Product, Double> discountPerProduct;

    private DicountPolicy discount_operand1;
    private DicountPolicy discount_operand2;
    private DiscountOperator operator;

    public DiscountPolicyComp(DicountPolicy discount_operands1, DicountPolicy discount_operands2, DiscountOperator operator, DiscountBInterface result){
        //this.discountPerProduct = new HashMap<>();
        this.discount_operand1 = discount_operands1;
        this.discount_operand1 = discount_operands2;
        this.operator = operator;

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

