package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.Store;

import java.util.ArrayList;
import java.util.List;

public class DiscountCondBasketProducts implements DiscountBInterface {


    private int discountID;
    private Product productCondition;
    private int minAmount;
    private Product discountProduct;
    private int discount;
    private boolean simple;

    public DiscountCondBasketProducts(int discountID, Product productCondition, Product discountProduct, int amount, int discount, boolean onAll) {
        this.discountID = discountID;
        this.discountProduct= discountProduct;
        this.productCondition = productCondition;
        this.minAmount= amount;
        this.discount = discount;
        this.simple = true;
    }

    public boolean isSimple() {
        return simple;
    }

    public Product getProductCondition() {
        return productCondition;
    }
    public Product getProductDiscount() {
        return discountProduct;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getDiscount() {
        return discount;
    }
    /*
    public boolean isOnAllTheAmount() {
        return onAllTheAmount;
    }
    */

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
        int amount = basket.getProductAmount(productCondition.getName());
        if(amount>minAmount)
            return true;
        return false;
    }

    @Override
    public  double calc(Basket basket, double price){
        int amount = basket.getProductAmount(productName);
        double sum = 0;
        if (!onAllTheAmount){
            for(int i = amount; i>0; i--){
                if( i%(minAmount+1) == 0 ){
                    sum = sum + (price - (price * (discount/100)));
                }
                else{
                    sum = sum + price;
                }
            }
        }

        else{
            sum = (price * amount) - ((price * amount) * (discount/100));
        }

        return sum;
    }

    @Override
    public String discountDescription() {
        String output = "";
        if(onAllTheAmount){
            output = "discount of: " + discount + "% on product: " + discountProduct.getName() + " if the amount of " + productCondition.getName() + " is more then: " + minAmount;
        }
        else{
            int amount = minAmount+1;
            output = "discount of: " + discount + "% on product: " + productName + " on the : " + amount + "product" ;
        }
        return output;
    }
}
