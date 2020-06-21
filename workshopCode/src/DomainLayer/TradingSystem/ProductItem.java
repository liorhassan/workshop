package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "productItems")
public class ProductItem implements Serializable {


    @Id
    @Column(name="id", unique = true)
    @GeneratedValue
    private int id;

    @Column(name = "product")
    private String productName;
    @Transient
    private Product product;

    @Column(name = "amount")
    private int amount;

    @Column(name = "basket")
    private int basketId;
    @Transient
    private Basket basket;

    public ProductItem(){}
    public ProductItem(Product product, int amount, Basket basket) {
        this.product = product;
        this.productName = product.getName();
        this.amount = amount;
        this.basket = basket;
        this.basketId = basket.getId();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productName = product.getName();
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

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String name) {
        this.productName = name;
    }

    public void setBasketId(int id) {
        this.basketId = id;
    }
}
