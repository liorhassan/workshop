package ExternalSystems;

import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

public class PaymentCollectionProxy implements PaymentCollection {

    private PaymentCollection paymentCollection;

    public PaymentCollectionProxy() {
        this.paymentCollection = new PaymentCollectionStub();
    }


    public boolean pay(double totalPrice, User user) {
        return this.paymentCollection.pay(totalPrice, user);
    }
}