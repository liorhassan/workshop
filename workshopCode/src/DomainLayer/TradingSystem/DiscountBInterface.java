package DomainLayer.TradingSystem;

public interface DiscountBInterface {
    public boolean canGet(int basketPrice);
    public double calc(int basketPrice);
}
