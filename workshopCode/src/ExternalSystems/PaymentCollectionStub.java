package ExternalSystems;

import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

public class PaymentCollectionStub implements PaymentCollection{

    public boolean pay(double totalCheck, User user){
        if(totalCheck > 1000){
            return false;
        }
        return true;
    }
}
