package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

import java.util.ArrayList;
import java.util.List;

public class DiscountPolicyIf implements DicountPolicy{
    private DiscountBInterface result;
    private DicountPolicy condition;


    public DiscountPolicyIf(DiscountBInterface result, DicountPolicy condition){
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
