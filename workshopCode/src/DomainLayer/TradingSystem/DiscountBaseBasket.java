package DomainLayer.TradingSystem;

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
    public boolean canGet(int basketPrice){
        if(basketPrice>sum){
            return true;
        }
        return false;
    }
    @Override
    public double calc(double basketPrice){
        return (basketPrice - (basketPrice * (discount/100)));
    }
}
