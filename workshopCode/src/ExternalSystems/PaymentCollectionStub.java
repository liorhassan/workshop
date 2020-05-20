package ExternalSystems;

import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

public class PaymentCollectionStub implements PaymentCollection{

    public boolean pay(ShoppingCart sc, User user){
        if(sc.getTotalCartPrice() >= 1000)
            return false;
        return true;
    }
}
