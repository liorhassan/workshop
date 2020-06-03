package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

import java.util.ArrayList;
import java.util.List;

public class DiscountPolicyAnd extends DiscountPolicy {
    //private int discountID;
    private DiscountBInterface discount_operand1;
    private DiscountBInterface discount_operand2;
    private boolean simple;

    public DiscountPolicyAnd( DiscountBInterface discount_operands1, DiscountBInterface discount_operands2){
        //this.discountID = discountID;
        this.discount_operand1 = discount_operands1;
        this.discount_operand2 = discount_operands2;
        this.simple = false;

    }
    @Override
    public List<DiscountBInterface> filterDiscounts(Basket basket) {
        return null;
    }

    public DiscountBInterface getOperand1(){
        return discount_operand1;
    }

    public DiscountBInterface getOperand2(){
        return discount_operand2;
    }
    @Override
    public boolean canGet(Basket basket) {
        if(discount_operand1.canGet(basket) && discount_operand1.canGet(basket)){
            return true;
        }
        return false;
    }

    @Override
    public String discountDescription() {
        return discount_operand1.discountDescription() + " AND " +discount_operand1.discountDescription();
    }


    //public int getDiscountID() {
    //        return discountID;
    //}

    @Override
    public int getDiscountPercent() {
        return 0;
    }

    @Override
    public boolean isSimple() {
        return isSimple();
    }

    @Override
    public List<DiscountBInterface> relevantDiscounts(Basket basket) {
        List<DiscountBInterface> output = new ArrayList<>();
        List<DiscountBInterface> discountsForOperand1 = discount_operand1.relevantDiscounts(basket);
        List<DiscountBInterface> discountsForOperand2 = discount_operand2.relevantDiscounts(basket);
        output = discountsForOperand1;
        output.addAll(discountsForOperand2);
        return output;
    }
}
