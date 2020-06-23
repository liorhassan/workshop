package ExternalSystems;

import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

import java.util.Hashtable;

public class PaymentCollectionProxy implements PaymentCollection {

    private PaymentCollection paymentCollection;

    public PaymentCollectionProxy() {
        this.paymentCollection = new PaymentCollectionStub();
    }


    public boolean pay(double totalPrice, User user) {
        return this.paymentCollection.pay(totalPrice, user);
    }

    public int pay(Hashtable<String, String> data) {
        return this.paymentCollection.pay(data);
    }

    public int cancelPayment(int transactionId) {
        return this.paymentCollection.cancelPayment(transactionId);
    }
    }