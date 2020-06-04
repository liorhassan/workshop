package DomainLayer.TradingSystem.Models;

import DataAccessLayer.PersistenceController;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "purchases")
public class Purchase implements Serializable {

    @Id
    @Column(name="id")
    @GeneratedValue
    private int id;

    @OneToOne
    @JoinColumn(name = "cart", referencedColumnName = "id")
    private ShoppingCart shoppingCart;

    @Column(name = "ownerName")
    private String ownerName;

    @Column(name = "isUsersPurchase")
    private boolean isUsersPurchase;

    // Ctor for user's purchase
    public Purchase(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
        this.isUsersPurchase = true;
        this.ownerName = shoppingCart.getUser().getUsername();
    }

    // Ctor for store's purchase
    public Purchase(Basket b, User u){
        this.isUsersPurchase = false;
        this.ownerName = b.getStore().getName();
        this.shoppingCart = new ShoppingCart(u);
        this.shoppingCart.setIsHistory();
        this.shoppingCart.addBasket(b);

        // save new shopping cart to db
        PersistenceController.create(this.shoppingCart);
    }
    public ShoppingCart getPurchasedProducts() {
        return shoppingCart;
    }

    public double getTotalCheck() {
        return this.shoppingCart.getTotalCartPrice();
    }
}
