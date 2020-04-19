package DomainLayer;

import java.util.LinkedList;
import java.util.List;

public class StorePurchaseHistory {

    private List<Purchase> purchases;
    private Store store;

    public StorePurchaseHistory(Store s){
        this.store = s;
        this.purchases = new LinkedList<>();
    }

    public void addPurchase(Purchase p){
        this.purchases.add(p);
    }

    public List<Purchase> getPurchases(){
        return this.purchases;
    }

}
