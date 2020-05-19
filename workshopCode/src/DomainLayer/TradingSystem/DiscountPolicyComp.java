package DomainLayer.TradingSystem;
import java.util.ArrayList;
import java.util.List;
import DomainLayer.TradingSystem.Models.Basket;

public class DiscountPolicyComp implements DiscountPolicy {

    //private HashMap<Product, Double> discountPerProduct;

    private DiscountPolicy discount_operand1;
    private DiscountPolicy discount_operand2;
    private PolicyOperator operator;

    public DiscountPolicyComp(DiscountPolicy discount_operands1, DiscountPolicy discount_operands2, PolicyOperator operator){
        //this.discountPerProduct = new HashMap<>();
        this.discount_operand1 = discount_operands1;
        this.discount_operand1 = discount_operands2;
        this.operator = operator;

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


    //public void addDiscount(Product p, Double percentage) {
    //   discountPerProduct.put(p, (percentage/100));
    //}
/*
    public double calcProductDiscount(Basket b){
        double totalDisc = 0;
        for(ProductItem pi: b.getProductItems()){
            Product p = pi.getProduct();
            int amount = pi.getAmount();
            if(this.discountPerProduct.containsKey(p)){
                totalDisc += (amount * (p.getPrice() * this.discountPerProduct.get(p)));
            }
        }
        return totalDisc;
    }

 */
}

