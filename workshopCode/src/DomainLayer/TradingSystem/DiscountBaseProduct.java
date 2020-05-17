package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.Store;

public class DiscountBaseProduct implements DiscountBInterface {

    private String productName;
    private int minAmount;
    private int discount;
    private boolean onAllTheAmount; // or on the minAmount + 1

    public DiscountBaseProduct(String productName, int amount, int discount, boolean onAll) {
        this.productName= productName;
        this.minAmount= amount;
        this.discount = discount;
        this.onAllTheAmount = onAll;
    }
    public String getProductName() {
        return productName;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getDiscount() {
        return discount;
    }

    public boolean isOnAllTheAmount() {
        return onAllTheAmount;
    }

    @Override
    public boolean canGet(int amount) {
        if(amount>minAmount)
            return true;
        return false;
    }

    public  double calc(int amount, double price){
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
}
