package UnitTests;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.Models.*;
import DomainLayer.TradingSystem.StoreOwning;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class PurchaseTest {

    Purchase purchase;

    @BeforeClass
    public static void init() {
        PersistenceController.initiate(false);
    }

    @Before
    public void setUp() throws Exception {
        User user0 = new User();
        user0.setUsername("tester0");
        purchase = new Purchase(new ShoppingCart(user0));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getTotalCheck() {
        assertEquals(0,purchase.getTotalCheck(),0.0000000001);
        Product p1 = new Product("Shirt",null,null,40.0,"Fox",1);
        Product p2 = new Product("Dress",null,null,45.5,"Fox",1);
        PersistenceController.create(p1);
        PersistenceController.create(p2);
        User user = new User();
        user.setUsername("tester");
        StoreOwning storeOwning = new StoreOwning("Fox", "tester");
        Store store = new Store("Fox",null,user,storeOwning);
        PersistenceController.create(store);
        store.getInventory().put(p1,10);
        store.getInventory().put(p2,10);
        purchase.getPurchasedProducts().addProduct("Shirt",store,1, true);
        assertEquals(40,purchase.getTotalCheck(),0.0000000001);
        purchase.getPurchasedProducts().addProduct("Dress",store,3, true);
        assertEquals(40+3*45.5,purchase.getTotalCheck(),0.0000000001);
    }
}