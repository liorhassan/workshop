package ExternalSystems;

import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

public interface ProductSupply {

    boolean supply(ShoppingCart sc, User user);

}
