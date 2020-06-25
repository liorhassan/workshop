package UnitTests;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.Category;
import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.Store;
import DomainLayer.TradingSystem.Models.User;
import DomainLayer.TradingSystem.ProductItem;
import DomainLayer.TradingSystem.StoreManaging;
import DomainLayer.TradingSystem.StoreOwning;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class StoreTest {

    Store store;

    @BeforeClass
    public static void init() {
        PersistenceController.initiate(false);
    }

    @Before
    public void setUp() throws Exception {
        User user = new User();
        user.setUsername("tester");
        StoreOwning storeOwning = new StoreOwning("Fox", "tester");
        store = new Store("Fox","clothsss",user,storeOwning);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void checkIfProductAvailable() {
        assertFalse(store.checkIfProductAvailable("Shirt",1));
        Product p = new Product("Shirt",null,null,40.0, "Fox",1);
        Product p2 = new Product("Dress",null,null,45.5, "Fox",1);
        store.getInventory().put(p,10);
        store.getInventory().put(p2,10);
        assertTrue(store.checkIfProductAvailable("Shirt",7));
        assertFalse(store.checkIfProductAvailable("Dress",11));
    }

    @Test
    public void getProductByName() {
        assertNull(store.getProductByName("Shirt"));
        Product p = new Product("Shirt",null,null,40.0, "Fox",1);
        Product p2 = new Product("Dress",null,null,45.5,"Fox",1);
        store.getInventory().put(p,10);
        store.getInventory().put(p2,10);
        assertEquals(p,store.getProductByName("Shirt"));
        assertEquals(p2,store.getProductByName("Dress"));
    }

    @Test
    public void getAppointer() throws SQLException {
        User user1 = new User();
        assertNull(store.getAppointer(user1));
        User user2 = new User();
        StoreManaging sm = new StoreManaging(user2, store.getName(), user1.getUsername());
        store.addManager(user1,sm, false);
        assertEquals(user2,store.getAppointer(user1));
    }

    @Test
    public void hasProduct() {
        assertFalse(store.hasProduct("Shirt"));
        assertFalse(store.hasProduct("Dress"));
        Product p = new Product("Shirt",null,null,40.0,"Fox",1);
        Product p2 = new Product("Dress",null,null,45.5,"Fox",1);
        store.getInventory().put(p,10);
        store.getInventory().put(p2,10);
        assertTrue(store.hasProduct("Shirt"));
        assertTrue(store.hasProduct("Dress"));
    }

    @Test
    public void updateInventory() throws SQLException {
        Product p = new Product("Shirt",null,null,40.0,"Fox",1);
        Product p2 = new Product("Dress",null,null,45.5, "Fox",1);
        store.getInventory().put(p,10);
        store.getInventory().put(p2,10);
        store.updateInventory("Shirt",30.0,null,"test",7);
        assertEquals(30.0,p.getPrice(),0.0000000001);
        assertEquals("test",p.getDescription());
        assertEquals(7,(long)store.getInventory().get(p));
    }

    @Test
    public void purchaseProduct() throws SQLException {
        Product p = new Product("Shirt",null,null,40.0,"Fox",1);
        Product p2 = new Product("Dress",null,null,45.5,"Fox",1);
        PersistenceController.create(p);
        PersistenceController.create(p2);
        store.getInventory().put(p,10);
        store.getInventory().put(p2,10);
        Basket b = new Basket();
        b.setStore(store);
        b.addProduct(p, 3);
        b.addProduct(p2, 10);

        store.reserveProduct(b.getProductItemByProduct(p), b);
        assertEquals(7,(int)store.getInventory().get(p));
        b.addProduct(p, 10);
        store.reserveProduct(b.getProductItemByProduct(p), b);
        assertEquals(7,(int)store.getInventory().get(p));
        store.reserveProduct(b.getProductItemByProduct(p2), b);
        assertFalse(store.getInventory().containsKey(p));
    }
}