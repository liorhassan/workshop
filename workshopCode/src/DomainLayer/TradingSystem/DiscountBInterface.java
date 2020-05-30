package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

import java.util.List;

public interface DiscountBInterface {
    public boolean canGet(Basket basket);
    public double calc(Basket basket, double price);
    public String discountDescription();
    public int getDiscountID();
    public int getDiscountPercent();
    public boolean isSimple();
    public List<DiscountBInterface> relevantDiscounts (Basket basket);
}


