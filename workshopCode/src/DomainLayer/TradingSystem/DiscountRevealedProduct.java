package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;

import java.util.ArrayList;
import java.util.List;

public class DiscountRevealedProduct  implements DiscountBInterface{
    private int discountId;
    private Product product;
    private int discount;
    private boolean simple;

    public DiscountRevealedProduct(Product product, int discount, int discountId) {
        this.product = product;
        this.discount = discount;
        this.discountId = discountId;
        this.simple = true;
    }

    public boolean isSimple() {
        return simple;
    }

    @Override
    public List<DiscountBInterface> relevantDiscounts (Basket basket){
        List<DiscountBInterface> output = new ArrayList<>();
        output.add(this);
        return output;
    }

    @Override
    public boolean canGet(Basket basket) {
        if(basket.getProductItems().contains(product))
            return true;
        return false;
    }

    @Override
    public double calc(Basket basket, double price) {
        return 0;
    }

    @Override
    public String discountDescription() {
        return "discount of: " + discount + "% on product: " + product.getName();
    }

    @Override
    public int getDiscountID() {
        return discountId;
    }
}
