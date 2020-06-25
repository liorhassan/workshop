package UnitTests;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.Models.*;
import DomainLayer.TradingSystem.ProductItem;
import DomainLayer.TradingSystem.StoreOwning;
import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class BasketTest {

    Basket basket;
    Store store;

    @BeforeClass
    public static void init() throws Exception {
        PersistenceController.initiate(false);
        UUID session_id = (new SessionHandler()).openNewSession();
        (new StoreHandler()).resetStores();
        (new UsersHandler()).register("ben", "toya");
    }

    @Before
    public void setUp() throws Exception {

        store = new Store("Fox",null,SystemFacade.getInstance().getUserByName("ben"),new StoreOwning());
        basket = new Basket(store, new ShoppingCart(new User()));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void viewBasket() {
        assertEquals("[]",basket.viewBasket().toString());
        Product p1 = new Product("Shirt",null,null,40.0,"Fox", 1);
        PersistenceController.create(p1);
        ProductItem pi = new ProductItem(p1,1,basket);
        PersistenceController.create(pi);
        basket.getProductItems().add(pi);
        assertEquals("[{\"amount\":1,\"price\":40.0,\"name\":\"Shirt\",\"store\":\"Fox\"}]",basket.viewBasket().toString());

        Product p2 = new Product("Dress",null,null,45.5,"Fox", 1);
        ProductItem pi2 = new ProductItem(p2,2,basket);

        basket.getProductItems().add(pi2);
        assertEquals("[{\"amount\":1,\"price\":40.0,\"name\":\"Shirt\",\"store\":\"Fox\"},{\"amount\":2,\"price\":45.5,\"name\":\"Dress\",\"store\":\"Fox\"}]",basket.viewBasket().toString());
    }

    @Test
    public void addProduct() {
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