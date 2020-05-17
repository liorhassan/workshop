package DomainLayer.TradingSystem;

import java.util.ArrayList;
import java.util.List;

public class DiscountPolicyIf implements DicountPolicy{
    private DiscountBInterface result;
    private DicountPolicy condition;


    public DiscountPolicyIf(DiscountBInterface result, DicountPolicy condition){
        this.condition = condition;
        this.result = result;
    }

}
