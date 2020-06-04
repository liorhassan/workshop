package DomainLayer.TradingSystem.Models;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.ProductItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "baskets")
public class Basket implements Serializable {

    @Id
    @Column(name="id")
    @GeneratedValue
    private int id;

//    @ManyToOne
    @JoinColumn(name = "store", referencedColumnName = "name")
    private Store store;


//    @ManyToOne
//@JoinColumn(name = "cart")
    @JoinColumn(name = "sc", referencedColumnName = "id")
    private ShoppingCart sc;

    @Transient
    private List<ProductItem> productItems;

    public Basket(Store store, ShoppingCart sc) {
        this.store = store;
        this.sc = sc;
        initProductItems();
    }

    private void initProductItems() {
        productItems = PersistenceController.readAllProductItems(id);
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public List<ProductItem> getProductItems() {
        return productItems;
    }

    public int getProductAmount(String productName){
        int amount = 0;
        for (ProductItem pi : getProductItems()) {
            if (pi.getProduct().equals(getStore().getProductByName(productName))) {
                amount = pi.getAmount();
                break;
            }
        }
        return amount;
    }

    public double calcPrice(){
        double total = 0;
        for (ProductItem pi: getProductItems()) {
            total += (pi.getAmount() * pi.getProduct().getPrice());
        }
        return total;
    }

    public void setProductItems(List<ProductItem> productItems) {
        this.productItems = productItems;
    }

    public JSONArray viewBasket() {
//        if (productItems.isEmpty())                          // if no products return nothig (במיוחד בשביל הפונקציה של ההצגה של השופינגכארט)
//            return "";
//        String output = "Store name: " + this.store.getName() + "\n";
//        for (ProductItem pi : productItems) {
//            output = output + "Product name: " + pi.getProduct().getName() + " price: " + pi.getProduct().getPrice() + " amount: " + pi.getAmount() + "\n";
//        }
//        return output;
        JSONArray basketArray = new JSONArray();
        if(productItems.isEmpty())
            return basketArray;

        for (ProductItem pi : productItems) {
            JSONObject currProduct = new JSONObject();
            currProduct.put("name", pi.getProduct().getName());
            currProduct.put("price", pi.getProduct().getPrice());
            currProduct.put("store", this.store.getName());
            currProduct.put("amount", pi.getAmount());
            basketArray.add(currProduct);
        }
        return basketArray;
    }


    public void addProduct(Product p, int amount) {
        for (ProductItem pi : productItems) {
            if (pi.getProduct().equals(p)) {
                pi.setAmount(pi.getAmount() + amount);
                //update DB
                PersistenceController.update(pi);
                return;
            }
        }

        ProductItem pi = new ProductItem(p, amount, this);
        productItems.add(pi);
        //create pi in DB
        PersistenceController.create(pi);
    }

    public void reserve(){
        store.reserveBasket(this);
    }

    public void unreserve(){
        if(!store.getReservedProducts(this).isEmpty()) {
            //there are reserved products in the basket that needs to be returned
            store.unreserveBasket(this);
        }
    }
}
