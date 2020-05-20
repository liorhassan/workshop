package ExternalSystems;

import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

public class ProductSupplyStub implements ProductSupply{

    public boolean supply(ShoppingCart sc, User user){
        if(user.getUsername().equals("zuzu"))
            return false;
        return true;
    }
}
