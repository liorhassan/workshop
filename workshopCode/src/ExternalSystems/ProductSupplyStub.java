package ExternalSystems;

import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

public class ProductSupplyStub implements ProductSupply{

    public boolean supply(ShoppingCart sc, User user){
        return true;
    }
}
