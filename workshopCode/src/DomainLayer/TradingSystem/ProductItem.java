package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "productItems")
public class ProductItem implements Serializable {


    @Id
    @Column(name="id")
    @GeneratedValue
    private int id;

//    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "product", referencedColumnName = "name")
    private Product product;

    @Column(name = "amount")
    private int amount;

//    @ManyToOne()
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "basket", referencedColumnName = "id")
    private Basket basket;

    public ProductItem(){}
    public ProductItem(Product product, int amount, Basket basket) {
        this.product = product;
        this.amount = amount;
        this.basket = basket;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }

}
