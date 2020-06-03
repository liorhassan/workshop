package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;

public abstract class DiscountSimple implements DiscountBInterface {
    public abstract Product getProductDiscount();
    public abstract double calc(Basket basket);
    public abstract int getDiscountID();
}
