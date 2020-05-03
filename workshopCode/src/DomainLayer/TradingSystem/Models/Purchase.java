package DomainLayer.TradingSystem.Models;

public class Purchase {

    private ShoppingCart shoppingCart;

    public Purchase(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public ShoppingCart getPurchasedProducts() {
        return shoppingCart;
    }


    public double getTotalCheck() {
        return this.shoppingCart.getTotalCartPrice();
    }
}
