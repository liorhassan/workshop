package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;

public class PurchasePolicyProduct implements PurchasePolicy {
    private String productName;
    private int amount;
    private boolean minOrmax;
    private int purchaseId;


    public PurchasePolicyProduct(String productName, int amount, boolean minOrmax, int purchaseId) {
        this.purchaseId = purchaseId;
        this.productName = productName;
        this.amount = amount;
        this.minOrmax = minOrmax;
    }


    @Override
    public boolean purchaseAccordingToPolicy(Basket b) {
        for(ProductItem pi : b.getProductItems()){
            if(pi.getProduct().getName().equals(productName)){
                if(minOrmax){
                    return (pi.getAmount() <= amount);
                }
                else
                    return (pi.getAmount()>= amount);
            }
        }
        return true;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

}
