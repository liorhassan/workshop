package UnitTests;

import DomainLayer.Models.Basket;
import DomainLayer.Models.Product;
import DomainLayer.Models.Store;
import DomainLayer.ProductItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BasketTest {

    Basket basket;
    Store store;
    @Before
    public void setUp() throws Exception {
        store = new Store("Fox",null,null,null);
        basket = new Basket(store);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void viewBasket() {
        assertEquals("",basket.viewBasket());
        basket.getProductItems().add(new ProductItem(new Product("Shirt",null,null,40.0),1,basket));
        assertEquals("Store name: Fox\nProduct name: Shirt price: 40.0 amount: 1\n",basket.viewBasket());
        basket.getProductItems().add(new ProductItem(new Product("Dress",null,null,45.4),2,basket));
        assertEquals("Store name: Fox\nProduct name: Shirt price: 40.0 amount: 1\nProduct name: Dress price: 45.5 amount 2\n",basket.viewBasket());
    }

    @Test
    public void addProduct() {
        ArrayList<ProductItem> excpected = new ArrayList<>();
        assertEquals(excpected,basket.getProductItems());
        Product p = new Product("Shirt",null,null,40.0);
        ProductItem pi = new ProductItem(p,1,basket);
        excpected.add(pi);
        basket.addProduct(p,1);
        assertEquals(excpected,basket.getProductItems());
        Product p2 = new Product("Dress",null,null,45.5);
        ProductItem pi2 = new ProductItem(p2,1,basket);
        excpected.add(pi2);
        basket.addProduct(p2,1);
        assertEquals(excpected,basket.getProductItems());
        basket.addProduct(p,4);
        excpected.get(0).setAmount(5);
        assertEquals(excpected,basket.getProductItems());
    }


}