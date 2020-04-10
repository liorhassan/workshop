package DomainLayer;

public class User {

    private ShoppingCart shoppingCart;

    public User(){
        shoppingCart = new ShoppingCart(this);
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }
}
