package DomainLayer.TradingSystem;
import DomainLayer.TradingSystem.Models.Basket;

import java.util.List;
public abstract class DiscountPolicy implements DiscountBInterface {
    public abstract List<DiscountBInterface> filterDiscounts(Basket basket);
    public abstract DiscountBInterface getOperand1();
    public abstract DiscountBInterface getOperand2();
}
