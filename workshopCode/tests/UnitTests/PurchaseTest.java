package UnitTests;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.Purchase;
import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.Store;
import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class PurchaseTest {

    Purchase purchase;

    @BeforeClass
    public static void init() throws Exception {
        PersistenceController.initiate(false);
        UUID session_id = (new SessionHandler()).openNewSession();
        (new StoreHandler()).resetStores();
        (new UsersHandler()).register("ben", "toya");
    }

    @Before
    public void setUp() throws Exception {
        purchase = new Purchase(new ShoppingCart(SystemFacade.getInstance().getUserByName("ben")));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getTotalCheck() throws SQLException {
        assertEquals(0,purchase.getTotalCheck(),0.0000000001);
        Product p1 = new Product("Shirt",null,null,40.0,"Fox",1);
        Product p2 = new Product("Dress",null,null,45.5,"Fox",1);
        Store store = new Store("Fox",null,null,null);
        store.getInventory().put(p1,10);
        store.getInventory().put(p2,10);
        purchase.getPurchasedProducts().addProduct("Shirt",store,1, true);
        assertEquals(40,purchase.getTotalCheck(),0.0000000001);
        purchase.getPurchasedProducts().addProduct("Dress",store,3, true);
        assertEquals(40+3*45.5,purchase.getTotalCheck(),0.0000000001);
    }
}