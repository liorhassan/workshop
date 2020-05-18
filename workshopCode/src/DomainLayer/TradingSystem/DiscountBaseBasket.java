package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

public class DiscountBaseBasket implements DiscountBInterface {

    private int sum;
    private int discount;
    private boolean onPrice;        //or amount of products in the basket


    public DiscountBaseBasket(int sum, int discount, boolean onPrice) {
        this.sum = sum;
        this.discount = discount;
        this.onPrice = onPrice;
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
}
