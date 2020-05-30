package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.Store;

import java.util.ArrayList;
import java.util.List;

public class DiscountCondBasketProducts extends DiscountSimple {


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

    public int getDiscountPercent() {
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

    public  double calc(Basket basket){
        ProductItem pi = basket.getProductItemByProduct(discountProduct);
        double price = pi.getAmount() * (pi.getProduct().getPrice() - (pi.getProduct().getPrice()*discount / 100));
        return  price;
    }

    @Override
    public String discountDescription() {
        String output = "discount of: " + discount + "% on product: " + discountProduct.getName() + " if the amount of " + productCondition.getName() + " is more then: " + minAmount;

        return output;
    }
}
