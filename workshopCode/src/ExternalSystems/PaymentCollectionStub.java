package ExternalSystems;

import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

public class PaymentCollectionStub implements PaymentCollection{

    public boolean pay(ShoppingCart sc, User user){
        return true;
    }
}
