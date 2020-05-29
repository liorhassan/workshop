package DomainLayer.TradingSystem;
import java.util.ArrayList;
import java.util.List;
import DomainLayer.TradingSystem.Models.Basket;

public class DiscountPolicyXor implements DiscountBInterface {

    //private HashMap<Product, Double> discountPerProduct;
    private int discountID;
    private DiscountBInterface discount_operand1;
    private DiscountBInterface discount_operand2;
    private PolicyOperator operator;
    private boolean simple;

    public DiscountPolicyXor(int discountID, DiscountBInterface discount_operands1, DiscountBInterface discount_operands2, PolicyOperator operator){
        this.discountID = discountID;
        this.discount_operand1 = discount_operands1;
        this.discount_operand2 = discount_operands2;
        this.operator = operator;
        this.simple = false;

    }

    public boolean isSimple() {
        return simple;
    }

    @Override
    public List<DiscountBInterface> checkDiscounts(List<DiscountBInterface> discounts, Basket basket) {
        List<DiscountBInterface> disc1 = discount_operand1.checkDiscounts(discounts, basket);
        List<DiscountBInterface> disc2 = discount_operand2.checkDiscounts(discounts, basket);
        List<DiscountBInterface> output = new ArrayList<>();



        switch(operator){
            case OR:
                output = disc1;
                for(DiscountBInterface dis : disc2){
                    if(!output.contains(dis))
                        output.add(dis);
                }
                break;
            case AND:
                for(DiscountBInterface dis : disc2){
                    if(disc1.contains(dis))
                        output.add(dis);
                }
                break;
            case XOR:
                for(DiscountBInterface dis : disc2){
                    if(!disc1.contains(dis))
                        output.add(dis);
                }
                for(DiscountBInterface dis : disc1){
                    if(!disc2.contains(dis))
                        output.add(dis);
                }
                break;
        }
        return output;
    }


    @Override
    public boolean canGet(Basket basket) {
        if(discount_operand1.canGet(basket) || discount_operand1.canGet(basket)){
            return true;
        }
        return false;
    }
    public List<DiscountBInterface> relevantDiscounts(Basket basket){
        if(discount_operand1.isSimple() && !(discount_operand1 instanceof DiscountBasketPriceOrAmount)){
            basket.getDiscountsOnProducts().contains()
        }
        List<DiscountBInterface> discountsForOperand1 = discount_operand1.relevantDiscounts(basket);
        List<DiscountBInterface> discountsForOperand2 = discount_operand2.relevantDiscounts(basket);

    }

    public List<DiscountBInterface> relevantDiscounts (List<DiscountBInterface> discounts){
        List<DiscountBInterface> discountsForOperand1 = discount_operand1.relevantDiscounts(discounts);
        List<DiscountBInterface> discountsForOperand2 = discount_operand2.relevantDiscounts(discounts);

    }

    @Override
    public double calc(Basket basket, double price) {
        return 0;
    }

    @Override
    public String discountDescription() {
        return discount_operand1.discountDescription() + " XOR " +discount_operand1.discountDescription();
    }

    @Override
    public int getDiscountID() {
        return discountID;
    }
}

