package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

import java.util.ArrayList;
import java.util.List;

public class DiscountPolicyIf implements DiscountBInterface {
    private int discountID;
    private DiscountBInterface result;
    private DiscountBInterface condition;
    private boolean simple;


    public DiscountPolicyIf(DiscountBInterface result, DiscountBInterface condition, int discountID){
        this.discountID = discountID;
        this.condition = condition;
        this.result = result;
        this.simple = false;
    }


    public boolean isSimple() {
        return simple;
    }


    public DiscountBInterface getResult() {
        return result;
    }

    public boolean canGet( Basket basket) {
        if(condition.canGet(basket)){
            return true;
        }
        return false;
    }

    @Override
    public double calc(Basket basket, double price) {
        return 0;
    }

    @Override
    public String discountDescription() {
        String output = "If you deserve : " + condition.discountDescription() + " then you deserve : " + result.discountDescription();
        return output;
    }

    public List<DiscountBInterface> relevantDiscounts (Basket basket){
        List<DiscountBInterface> output = new ArrayList<>();
        List<DiscountBInterface> conditionDiscounts = condition.relevantDiscounts(basket);
        List<DiscountBInterface> resultDiscounts = condition.relevantDiscounts(basket);
        if(canGet(basket)){
            output = conditionDiscounts;
            output.addAll(resultDiscounts);
        }
        return output;
    }

    @Override
    public int getDiscountID() {
        return discountID;
    }

}
