package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

import java.util.ArrayList;
import java.util.List;


public class DiscountBasketPriceOrAmount implements DiscountBInterface {

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

    @Override
    public boolean canGet(Basket basket ){
        if(onPrice){
            int param =
        }
        else{

        }
        if (param > sum) {
            return true;
        }
        return false;
    }
    @Override
    public double calc(Basket basket, double basketPrice){
        return (basketPrice - (basketPrice * (discount/100)));
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
