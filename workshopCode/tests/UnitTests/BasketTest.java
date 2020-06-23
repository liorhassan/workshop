package UnitTests;

import DomainLayer.TradingSystem.Models.*;
import DomainLayer.TradingSystem.ProductItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class BasketTest {

    Basket basket;
    Store store;
    @Before
    public void setUp() throws Exception {
        store = new Store("Fox",null,null,null);
        basket = new Basket(store, new ShoppingCart(new User()));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void viewBasket() {
        assertEquals("",basket.viewBasket());
        basket.getProductItems().add(new ProductItem(new Product("Shirt",null,null,40.0,"Fox", 1),1,basket));
        assertEquals("Store name: Fox\nProduct name: Shirt price: 40.0 amount: 1\n",basket.viewBasket());
        basket.getProductItems().add(new ProductItem(new Product("Dress",null,null,45.5,"Fox", 1),2,basket));
        assertEquals("Store name: Fox\nProduct name: Shirt price: 40.0 amount: 1\nProduct name: Dress price: 45.5 amount: 2\n",basket.viewBasket());
    }

    @Test
    public void addProduct() throws SQLException {
        assertEquals(new ArrayList<>(),basket.getProductItems());
        Product p = new Product("Shirt",null,null,40.0,"Fox", 1);
        basket.addProduct(p,1);
        assertEquals(1,basket.getProductItems().size());
        assertEquals(p,basket.getProductItems().get(0).getProduct());
        assertEquals(1,basket.getProductItems().get(0).getAmount());
        Product p2 = new Product("Dress",null,null,45.5,"Fox",1);
        basket.addProduct(p2,3);
        assertEquals(2,basket.getProductItems().size());
        assertEquals(p2,basket.getProductItems().get(1).getProduct());
        assertEquals(3,basket.getProductItems().get(1).getAmount());
        basket.addProduct(p,4);
        assertEquals(2,basket.getProductItems().size());
        assertEquals(p,basket.getProductItems().get(0).getProduct());
        assertEquals(5,basket.getProductItems().get(0).getAmount());
    }


}