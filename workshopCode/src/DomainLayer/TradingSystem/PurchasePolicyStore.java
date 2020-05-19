package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

public class PurchasePolicyStore implements PurchasePolicy {

    //private boolean onPrice;
    private int limit;
    private boolean minOrMax;

    public PurchasePolicyStore( int limit, boolean minOrMax) {
        //this.onPrice = onPrice;
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
}
