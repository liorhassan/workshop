package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;

import java.util.ArrayList;
import java.util.List;


public class DiscountBasketPriceOrAmount extends DiscountSimple {

    private int discountID;
    private int sum;
    private int discount;
    private boolean onPrice;        //or amount of products in the basket
    private boolean simple;


    public DiscountBasketPriceOrAmount(int discountID, int sum, int discount, boolean onPrice) {
        this.discountID = discountID;
        this.sum = sum;
        this.discount = discount;
        this.onPrice = onPrice;
        this.simple = true;
    }

    public boolean isSimple() {
        return simple;
    }

    public List<DiscountBInterface> relevantDiscounts (Basket basket){
        List<DiscountBInterface> output = new ArrayList<>();
        output.add(this);
        return output;
    }

    public int getDiscountID() {
        return discountID;
    }
    public int getDiscountPercent() {
        return discount;
    }
    @Override
    public boolean canGet(Basket basket ) {
        if (onPrice) {
            double price = basket.getPrice();
            if (price > this.sum) {
                return true;
            }
        } else {
            int totalAmount = 0;
            for (ProductItem pi : basket.getProductItems()) {
                totalAmount = totalAmount + pi.getAmount();
            }
            if (totalAmount > sum) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Product getProductDiscount() {
        return null;
    }

    public double calc(Basket basket){
        double per = discount/100.0;
        double disc = (basket.getPrice() * (discount/100.0));
        double newPrice = basket.getPrice() - disc ;
        return newPrice;
    }

    @Override
    public String discountDescription() {
        String output = "";
        if(onPrice){
            output = "discount of: " +  discount + "% on basket with price more then:"+ sum ;
        }
        else{
            output = "discount of: " +  discount + "% on basket with amount of products more then:" + sum;
        }

        return output;
    }

}
