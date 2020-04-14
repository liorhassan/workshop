package DomainLayer;

public class ProductItem {

    private Product product;
    private int amount;
    private Basket basket;


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
