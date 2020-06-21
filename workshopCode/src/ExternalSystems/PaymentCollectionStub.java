package ExternalSystems;

import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

public class PaymentCollectionStub implements PaymentCollection{

    public boolean pay(double totalPrice, User user){
        if(totalPrice >= 1000)
            return false;
        return true;
    }
}
