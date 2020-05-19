package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

public interface DiscountBInterface {
    public boolean canGet(double param);
    public double calc(Basket basket, double price);
    public String discountDescription();
    public int getDiscountID();

}


