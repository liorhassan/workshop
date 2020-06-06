package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

public class PurchasePolicyStore implements PurchasePolicy {

    //private boolean onPrice;
    private int limit;
    private boolean minOrMax;

    private int purchaseId;

    public PurchasePolicyStore( int limit, boolean minOrMax, int purchaseId) {
        this.purchaseId = purchaseId;
        this.limit = limit;
        this.minOrMax = minOrMax;
    }

    @Override
    public boolean purchaseAccordingToPolicy(Basket b) {
        int amount = 0;
        for(ProductItem pi : b.getProductItems()){
            amount = amount + pi.getAmount();
        }
        if(minOrMax){
            return amount <= limit;
        }
        else
            return amount >= limit;
    }

    public int getPurchaseId() {
        return purchaseId;
    }
}
