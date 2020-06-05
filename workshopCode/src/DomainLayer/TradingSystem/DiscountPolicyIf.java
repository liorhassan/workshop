package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

import java.util.ArrayList;
import java.util.List;

public class DiscountPolicyIf extends DiscountPolicy  {
    //private int discountID;
    private DiscountBInterface result;
    private DiscountBInterface condition;
    private boolean simple;


    public DiscountPolicyIf( DiscountBInterface condition, DiscountBInterface result){
        //this.discountID = discountID;
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
        return false;
    }

    public DiscountBInterface getOperand1(){
        return condition;
    }

    public DiscountBInterface getOperand2(){
        return result;
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
            if(!result.isSimple())  {
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



    //public int getDiscountID() {
    //    return discountID;
    //}

    @Override
    public int getDiscountPercent() {
        return 0;
    }

}
