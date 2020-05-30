package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;

import java.util.ArrayList;
import java.util.List;

public class DiscountRevealedProduct  extends DiscountSimple{
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

    public double calc(Basket basket) {
        ProductItem pi = basket.getProductItemByProduct(product);
        double price = pi.getAmount() * (pi.getProduct().getPrice() - (pi.getProduct().getPrice()*discount / 100));
        return  price;
    }

    @Override
    public String discountDescription() {
        return "discount of: " + discount + "% on product: " + product.getName();
    }

    @Override
    public int getDiscountID() {
        return discountId;
    }

    public Product getProductDiscount() {
        return product;
    }
    @Override
    public int getDiscountPercent() {
        return discount;
    }

}
