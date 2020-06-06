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
        double priceIn1 = 0;
        double priceIn2 = 0;
        /*
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
            else if(basket.getDiscountsOnProducts().contains(discount_operand1))
                chosenDiscounts.add(discount_operand1);
            else if(basket.getDiscountsOnProducts().contains(discount_operand2))
                chosenDiscounts.add(discount_operand2);

        }
        else{

         */
            List<DiscountBInterface> chosenIn1 = new ArrayList<>();
            List<DiscountBInterface> chosenIn2 = new ArrayList<>();
            if(!discount_operand1.isSimple())
                chosenIn1 = ((DiscountPolicy) discount_operand1).filterDiscounts(basket);
            else if (basket.getDiscountsOnProducts().contains(discount_operand1))
                chosenIn1.add(discount_operand1);
            if(!discount_operand2.isSimple())
                chosenIn2 = ((DiscountPolicy) discount_operand2).filterDiscounts(basket);
            else if (basket.getDiscountsOnProducts().contains(discount_operand2))
                chosenIn2.add(discount_operand2);

            for(DiscountBInterface dis : chosenIn1){
                if(basket.getDiscountsOnProducts().contains(dis)){
                    priceIn1 = priceIn1 + ((DiscountSimple) dis).calc(basket);
                }
            }

            for(DiscountBInterface dis : chosenIn2){
                if(basket.getDiscountsOnProducts().contains(dis)){
                    priceIn2 = priceIn2 + ((DiscountSimple) dis).calc(basket);
                }
            }

            if((priceIn2 >= priceIn1) && priceIn1 !=0){
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

