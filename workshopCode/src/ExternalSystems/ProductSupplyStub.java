package ExternalSystems;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

import java.util.Collection;
import java.util.List;

public class ProductSupplyStub implements ProductSupply{

    public boolean supply(Collection<Basket> products, User user){
        if(user.getUsername() != null && user.getUsername().equals("zuzu")){
            return false;
        }
        return true;
    }
}
