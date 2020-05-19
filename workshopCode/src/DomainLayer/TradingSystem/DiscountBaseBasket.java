package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;


public class DiscountBaseBasket implements DiscountBInterface {

    private int discountID;
    private int sum;
    private int discount;
    private boolean onPrice;        //or amount of products in the basket


    public DiscountBaseBasket(int discountID, int sum, int discount, boolean onPrice) {
        this.discountID = discountID;
        this.sum = sum;
        this.discount = discount;
        this.onPrice = onPrice;
    }


    public int getDiscountID() {
        return discountID;
    }

    @Override
    public boolean canGet(double price ){
        if (price > sum) {
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