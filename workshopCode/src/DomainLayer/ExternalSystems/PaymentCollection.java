package DomainLayer.ExternalSystems;

import DomainLayer.Models.Purchase;
import DomainLayer.Models.ShoppingCart;
import DomainLayer.Models.User;

public class PaymentCollection {

    public boolean pay(ShoppingCart sc, User user){
        return true;
    }
}
