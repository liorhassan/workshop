package ExternalSystems;

import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

import java.util.Hashtable;

public interface PaymentCollection {

    boolean pay(double totalPrice, User user);

    int pay(Hashtable<String, String> data);

    int cancelPayment(int transactionId);
    }
