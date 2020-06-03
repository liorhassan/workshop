package DomainLayer.TradingSystem;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import DomainLayer.TradingSystem.Models.Basket;

public class DiscountPolicyXor extends DiscountPolicy {

    //private HashMap<Product, Double> discountPerProduct;
    //private int discountID;
    private DiscountBInterface discount_operand1;
    private DiscountBInterface discount_operand2;
    //private PolicyOperator operator;
    private boolean simple;

    public DiscountPolicyXor( DiscountBInterface discount_operands1, DiscountBInterface discount_operands2){
        //this.discountID = discountID;
        this.discount_operand1 = discount_operands1;
        this.discount_operand2 = discount_operands2;
        this.simple = false;

    }

    public boolean isSimple() {
        return simple;
    }



    @Override
    public boolean canGet(Basket basket) {
        if(discount_operand1.canGet(basket) || discount_operand1.canGet(basket)){
            return true;
        }
        return false;
    }

    public List<DiscountBInterface> filterDiscounts(Basket basket){
        List<DiscountBInterface> chosenDiscounts = new ArrayList<>();
        if(discount_operand1.isSimple() && discount_operand2.isSimple()){
            if(basket.getDiscountsOnProducts().contains(discount_operand1) && basket.getDiscountsOnProducts().contains(discount_operand2)){
                if(discount_operand1.getDiscountPercent()>=discount_operand2.getDiscountPercent()){
                    chosenDiscounts.add(discount_operand1);
                    basket.getDiscountsOnProducts().remove(discount_operand2);
                }
                else{
                    chosenDiscounts.add(discount_operand2);
                    basket.getDiscountsOnProducts().remove(discount_operand1);
                }
            }
        }
        else{
            List<DiscountBInterface> chosenIn1 = new ArrayList<>();
            List<DiscountBInterface> chosenIn2 = new ArrayList<>();
            if(DiscountPolicy.class.isAssignableFrom(discount_operand1.getClass()))
                chosenIn1 = ((DiscountPolicy) discount_operand1).filterDiscounts(basket);
            else
                chosenIn1.add(discount_operand1);
            if(DiscountPolicy.class.isAssignableFrom(discount_operand2.getClass()))
                chosenIn2 = ((DiscountPolicy) discount_operand2).filterDiscounts(basket);
            else
                chosenIn2.add(discount_operand1);
            if(chosenIn1.size() >= chosenIn2.size()){
                chosenDiscounts = chosenIn1;
                for (DiscountBInterface dis : chosenIn2){
                    if(basket.getDiscountsOnProducts().contains(dis))
                        basket.getDiscountsOnProducts().remove(dis);
                }
            }
            else{
                chosenDiscounts = chosenIn2;
                for (DiscountBInterface dis : chosenIn1){
                    if(basket.getDiscountsOnProducts().contains(dis))
                        basket.getDiscountsOnProducts().remove(dis);
                }
            }
        }
        return chosenDiscounts;
    }

    public List<DiscountBInterface> relevantDiscounts (Basket basket){
        List<DiscountBInterface> output = new ArrayList<>();
        List<DiscountBInterface> discountsForOperand1 = discount_operand1.relevantDiscounts(basket);
        List<DiscountBInterface> discountsForOperand2 = discount_operand2.relevantDiscounts(basket);
        output = discountsForOperand1;
        output.addAll(discountsForOperand2);
        return output;
    }


    @Override
    public String discountDescription() {
        return discount_operand1.discountDescription() + " XOR " +discount_operand1.discountDescription();
    }


    //public int getDiscountID() {
   //     return discountID;
    //}

    public DiscountBInterface getOperand1(){
        return discount_operand1;
    }

    public DiscountBInterface getOperand2(){
        return discount_operand2;
    }

    @Override
    public int getDiscountPercent() {
        return 0;
    }
}

