package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

import java.util.List;

public class DiscountPolicyIf implements DiscountPolicy {
    private DiscountBInterface result;
    private DiscountPolicy condition;


    public DiscountPolicyIf(DiscountBInterface result, DiscountPolicy condition){
        this.condition = condition;
        this.result = result;
    }

    public List<DiscountBInterface> checkDiscounts(List<DiscountBInterface> discounts, Basket basket) {
        List<DiscountBInterface> disc = condition.checkDiscounts(discounts, basket);
        if(disc.size() > 0){
            if(!disc.contains(result)){
              disc.add(result);
            }
        }
        return disc;
    }

}
