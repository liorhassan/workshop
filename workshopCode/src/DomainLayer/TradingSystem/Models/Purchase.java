package DomainLayer.TradingSystem.Models;

public class Purchase {

    private ShoppingCart shoppingCart;

    public Purchase(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public Purchase(Basket b, User u){
        this.shoppingCart = new ShoppingCart(u);
        this.shoppingCart.addBasket(b);
    }
    public ShoppingCart getPurchasedProducts() {
        return shoppingCart;
    }


    public double getTotalCheck() {
        return this.shoppingCart.getTotalCartPrice();
    }
}
