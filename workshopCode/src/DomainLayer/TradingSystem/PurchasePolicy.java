package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

public  interface PurchasePolicy {

    public boolean purchaseAccordingToPolicy(Basket b);
    public String getPurchaseDescription();

}
