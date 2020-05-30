package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;

import java.util.ArrayList;
import java.util.List;

public class DiscountCondProductAmount extends DiscountSimple{

    private int discountID;
    private int minAmount;
    private Product discountProduct;
    private int discount;
    private boolean simple;


    public DiscountCondProductAmount(int discountID, int minAmount, Product discountProduct, int discount) {
        this.discountID = discountID;
        this.minAmount = minAmount;
        this.discountProduct = discountProduct;
        this.discount = discount;
        this.simple = true;
    }

    public boolean isSimple() {
        return simple;
    }

    public Product getProductDiscount() {
        return discountProduct;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getDiscountPercent() {
        return discount;
    }

    public int getDiscountID() {
        return discountID;
    }

    @Override
    public List<DiscountBInterface> relevantDiscounts (Basket basket){
        List<DiscountBInterface> output = new ArrayList<>();
        output.add(this);
        return output;
    }

    @Override
    public boolean canGet( Basket basket) {
        int amount = basket.getProductAmount(getProductDiscount().getName());
        if(amount>minAmount)
            return true;
        return false;
    }

    public  double calc(Basket basket){
        int amount = basket.getProductAmount(discountProduct.getName());
        double sum = 0;
        for(int i = amount; i>0; i--){
            if( i%(minAmount+1) == 0 ){
                sum = sum + (discountProduct.getPrice() - (discountProduct.getPrice() * (discount/100)));
            }
            else{
                sum = sum + discountProduct.getPrice();
            }
        }

        return sum;
    }

    @Override
    public String discountDescription() {
        String output = "";
        int amount = minAmount+1;
        output = "discount of: " + discount + "% on product: " + discountProduct.getName() + " on the : " + amount + "product" ;

        return output;
    }
}
