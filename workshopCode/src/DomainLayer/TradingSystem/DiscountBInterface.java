package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

import java.util.List;

public interface DiscountBInterface {
    public boolean canGet(Basket basket);
    public String discountDescription();
    public int getDiscountPercent();
    public boolean isSimple();
    public List<DiscountBInterface> relevantDiscounts (Basket basket);
}


