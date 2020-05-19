package DomainLayer.TradingSystem;
import DomainLayer.TradingSystem.Models.Basket;

import java.util.List;
public interface DiscountPolicy {
    public List<DiscountBInterface> checkDiscounts(List<DiscountBInterface> discounts, Basket basket);
}
