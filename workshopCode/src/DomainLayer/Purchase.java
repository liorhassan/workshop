package DomainLayer;

public class Purchase {

    private ShoppingCart shoppingCart;

    public Purchase(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public ShoppingCart getPurchasedProducts() {
        return shoppingCart;
    }

    public double getTotalCheck() {
        double total = 0;
        for (Basket b: shoppingCart.getBaskets()) {
            for (ProductItem pi: b.getProductItems()) {
                total = total + (pi.getAmount() * pi.getProduct().getPrice());
            }
        }
        return total;
    }
}
