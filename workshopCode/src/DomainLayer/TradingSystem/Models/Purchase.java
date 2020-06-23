package DomainLayer.TradingSystem.Models;

import DataAccessLayer.PersistenceController;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.SQLException;

@Entity
@Table(name = "purchases")
public class Purchase implements Serializable {

    @Id
    @Column(name="id")
    @GeneratedValue
    private int id;


    @Column(name = "cart")
    private int cartId;

    @Transient
    private ShoppingCart shoppingCart;

    @Column(name = "ownerName")
    private String ownerName;

    @Column(name = "isUsersPurchase")
    private boolean isUsersPurchase;

    public Purchase(){}
    // Ctor for user's purchase
    public Purchase(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
        this.cartId = shoppingCart.getId();
        this.isUsersPurchase = true;
        this.ownerName = shoppingCart.getUser().getUsername();
    }

    // Ctor for store's purchase
    public Purchase(Basket b, User u) throws SQLException {
        this.isUsersPurchase = false;
        this.ownerName = b.getStoreName();
        this.shoppingCart = new ShoppingCart(u);
        this.shoppingCart.setIsHistory();
        Basket newBasket = new Basket(b.getStore(),shoppingCart);
        newBasket.setProductItems(b.getProductItems());

        // save new shopping cart to db
        PersistenceController.create(this.shoppingCart);
        this.cartId = shoppingCart.getId();
        newBasket.setCartId(cartId);
        PersistenceController.create(newBasket);
        newBasket.copyProductItems(b);
        this.shoppingCart.addBasket(newBasket);
        PersistenceController.update(this.shoppingCart);
        PersistenceController.update(newBasket);

    }
    public ShoppingCart getPurchasedProducts() {
        return shoppingCart;
    }

    public double getTotalCheck() {
        return this.shoppingCart.getTotalCartPrice();
    }

    public int getCartId() {
        return this.cartId;
    }

    public void setCart(ShoppingCart sc) {
        this.shoppingCart = sc;
    }
}
