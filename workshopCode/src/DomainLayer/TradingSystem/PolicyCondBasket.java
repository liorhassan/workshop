package DomainLayer.TradingSystem;

public class PolicyCondBasket {
    private boolean deserve;
    private boolean onPrice;
    private int limit;

    public PolicyCondBasket(boolean deserve, boolean onPrice, int limit) {
        this.deserve = deserve;
        this.onPrice = onPrice;
        this.limit = limit;
    }
}
