package ExternalSystems;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

import java.util.Collection;

public class ProductSupplyStub implements ProductSupply{

    public boolean supply(Collection<Basket> products, User user){
        if(user.isRegistred() && user.getUsername().equals("zuzu"))
            return false;
        return true;
    }
}
