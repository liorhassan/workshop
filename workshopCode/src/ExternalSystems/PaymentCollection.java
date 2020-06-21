package ExternalSystems;

import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

public interface PaymentCollection {

    boolean pay(double totalPrice, User user);

    }
