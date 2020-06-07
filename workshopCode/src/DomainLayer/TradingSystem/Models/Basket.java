package DomainLayer.TradingSystem.Models;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.DiscountBInterface;
import DomainLayer.TradingSystem.DiscountPolicy;
import DomainLayer.TradingSystem.DiscountSimple;
import DomainLayer.TradingSystem.ProductItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "baskets")
public class Basket implements Serializable {

    @Id
    @Column(name="id", unique = true)
    @GeneratedValue
    private int id;

//    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "store", referencedColumnName = "name")
    private Store store;


//    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "cart", referencedColumnName = "id")
    private ShoppingCart sc;

    @Transient
    private List<ProductItem> productItems;
    @Transient
    private double price;
    @Transient
    private List<DiscountBInterface> discountsOnProducts;
    @Transient
    private HashMap<ProductItem, Double> priceOfProdAfterDiscount;


    public Basket(){}
    public Basket(Store store, ShoppingCart sc) {
        this.store = store;
        productItems = new ArrayList<>();
        discountsOnProducts = new ArrayList<>();
        price = 0;
        priceOfProdAfterDiscount = new HashMap<>();
        this.sc = sc;
    }


    public List<DiscountBInterface> getDiscountsOnProducts() {
        return discountsOnProducts;
    }
    public void initProductItems() {
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

    public ProductItem getProductItemByProduct(Product product){
        for(ProductItem pi : productItems){
            if(pi.getProduct().equals(product)){
                return pi;
            }
        }
        return null;
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

    public void collectDiscounts(){
        for(DiscountBInterface dis : store.getDiscountsOnProducts()){
            if(dis.canGet(this)){
                discountsOnProducts.add(dis);
            }
        }
        for (DiscountBInterface policy : store.getDiscountPolicies()){
            if(policy.canGet(this)){
                ((DiscountPolicy)policy).filterDiscounts(this);
            }
        }

    }

    public void calcProductPrice(){
        for( DiscountBInterface dis : discountsOnProducts){

            ProductItem pi = getProductItemByProduct(((DiscountSimple) dis).getProductDiscount());
            Double newPrice = ((DiscountSimple)dis).calc(this);
            if(priceOfProdAfterDiscount.containsKey(pi)){
                double price = priceOfProdAfterDiscount.get(pi);
                if(newPrice < price){
                    priceOfProdAfterDiscount.replace(pi, newPrice);
                }
            }
            else{
                priceOfProdAfterDiscount.put(pi, newPrice);
            }

        }
    }
    public double calcBasketPrice(){
        double totalPrice = 0;
        collectDiscounts();
        calcProductPrice();
        for (ProductItem pi: productItems){
            if(priceOfProdAfterDiscount.containsKey(pi)){
                totalPrice = totalPrice + priceOfProdAfterDiscount.get(pi);
            }
            else{
                double price = pi.getAmount() * pi.getProduct().getPrice();
                totalPrice = totalPrice + price;
            }
        }
        this.price = totalPrice;
        return totalPrice;
    }

    public double calcBasketPriceBeforeDiscount(){
        double totalPrice = 0;
        for (ProductItem pi: productItems){
            double price = pi.getAmount() * pi.getProduct().getPrice();
            totalPrice = totalPrice + price;
        }

        return totalPrice;
    }

    public void setPrice(double price){
        this.price = price;
    }
    public double getPrice(){
        return  price;
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
