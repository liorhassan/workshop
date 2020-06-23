package ExternalSystems;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

import java.util.Collection;
import java.util.Hashtable;

public interface ProductSupply {

    boolean supply(Collection<Basket> products, User user);

    int supply(Hashtable<String, String> data);

    int cancelSupplement(int transactionId);

}

