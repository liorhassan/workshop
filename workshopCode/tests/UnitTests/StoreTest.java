package UnitTests;

import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.Store;
import DomainLayer.TradingSystem.Models.User;
import DomainLayer.TradingSystem.StoreManaging;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StoreTest {

    Store store;

    @Before
    public void setUp() throws Exception {
        store = new Store("Fox",null,null,null);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void checkIfProductAvailable() {
        assertFalse(store.checkIfProductAvailable("Shirt",1));
        Product p = new Product("Shirt",null,null,40.0);
        Product p2 = new Product("Dress",null,null,45.5);
        store.getInventory().put(p,10);
        store.getInventory().put(p2,10);
        assertTrue(store.checkIfProductAvailable("Shirt",7));
        assertFalse(store.checkIfProductAvailable("Dress",11));
    }

    @Test
    public void getProductByName() {
        assertNull(store.getProductByName("Shirt"));
        Product p = new Product("Shirt",null,null,40.0);
        Product p2 = new Product("Dress",null,null,45.5);
        store.getInventory().put(p,10);
        store.getInventory().put(p2,10);
        assertEquals(p,store.getProductByName("Shirt"));
        assertEquals(p2,store.getProductByName("Dress"));
    }

    @Test
    public void getAppointer() {
        User user1 = new User();
        assertNull(store.getAppointer(user1));
        User user2 = new User();
        StoreManaging sm = new StoreManaging(user2);
        store.addManager(user1,sm);
        assertEquals(user2,store.getAppointer(user1));
    }

    @Test
    public void hasProduct() {
        assertFalse(store.hasProduct("Shirt"));
        assertFalse(store.hasProduct("Dress"));
        Product p = new Product("Shirt",null,null,40.0);
        Product p2 = new Product("Dress",null,null,45.5);
        store.getInventory().put(p,10);
        store.getInventory().put(p2,10);
        assertTrue(store.hasProduct("Shirt"));
        assertTrue(store.hasProduct("Dress"));
    }

    @Test
    public void updateInventory() {
        Product p = new Product("Shirt",null,null,40.0);
        Product p2 = new Product("Dress",null,null,45.5);
        store.getInventory().put(p,10);
        store.getInventory().put(p2,10);
        store.updateInventory("Shirt",30.0,null,"test",7);
        assertEquals(30.0,p.getPrice(),0.0000000001);
        assertEquals("test",p.getDescription());
        assertEquals(7,(long)store.getInventory().get(p));
    }

    @Test
    public void purchaseProduct() {
        Product p = new Product("Shirt",null,null,40.0);
        Product p2 = new Product("Dress",null,null,45.5);
        store.getInventory().put(p,10);
        store.getInventory().put(p2,10);
        store.reserveProduct(p,3);
        assertEquals(7,(int)store.getInventory().get(p));
        store.reserveProduct(p,10);
        assertEquals(7,(int)store.getInventory().get(p));
        store.reserveProduct(p,7);
        assertFalse(store.getInventory().containsKey(p));
    }
}