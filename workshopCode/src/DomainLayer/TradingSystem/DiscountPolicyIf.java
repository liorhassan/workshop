package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

import java.util.ArrayList;
import java.util.List;

public class DiscountPolicyIf extends DiscountPolicy  {
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
        List<DiscountBInterface> resultDiscounts = condition.relevantDiscounts(basket);
        output = resultDiscounts;

        return output;
    }

    public List<DiscountBInterface> filterDiscounts (Basket basket){
        List<DiscountBInterface> chosenDiscounts = new ArrayList<>();
        if((condition.isSimple() && basket.getDiscountsOnProducts().contains(condition)) || (!condition.isSimple() && condition.canGet(basket))){
            if(DiscountPolicy.class.isAssignableFrom(result.getClass()) ) {
                chosenDiscounts =  ((DiscountPolicy) result).filterDiscounts(basket);
            }
            else {
                chosenDiscounts.add(result);
            }
        }
        else{
            if(result.isSimple() && basket.getDiscountsOnProducts().contains(result)){
                basket.getDiscountsOnProducts().remove(result);
            }
            else if (!result.isSimple()) {
                List<DiscountBInterface> discounts_to_delete = result.relevantDiscounts(basket);
                for (DiscountBInterface dis : discounts_to_delete) {
                    if (basket.getDiscountsOnProducts().contains(dis)) {
                        basket.getDiscountsOnProducts().remove(dis);
                    }
                }
            }

        }
        return chosenDiscounts;
    }


    @Override
    public int getDiscountID() {
        return discountID;
    }

    @Override
    public int getDiscountPercent() {
        return 0;
    }

}
