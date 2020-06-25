package UnitTests;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.Models.*;
import DomainLayer.TradingSystem.StoreOwning;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ShoppingCartTest {

    ShoppingCart sc;

    @BeforeClass
    public static void init() {
        PersistenceController.initiate(false);
    }

    @Before
    public void setUp() throws Exception {
        User user = new User();
        user.setUsername("tester");
        sc = new ShoppingCart(user);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addProduct() throws SQLException {
        assertTrue(sc.getBaskets().isEmpty());
        Product p = new Product("Shirt",null,null,40.0, "Fox",1);
        PersistenceController.create(p);
        User user1 = new User();
        user1.setUsername("tester1");
        StoreOwning storeOwning = new StoreOwning("Fox", "tester1");
        Store store = new Store("Fox",null,user1,storeOwning);
        PersistenceController.create(store);
        store.getInventory().put(p,10);
        sc.addProduct("Shirt",store,1, true);
        ArrayList<Basket> baskets = new ArrayList<>(sc.getBaskets());
        assertTrue(baskets.size()==1&&baskets.get(0).getProductItems().size()==1&&baskets.get(0).getProductItems().get(0).getProduct()==p&&baskets.get(0).getProductItems().get(0).getAmount()==1);
        sc.addProduct("Shirt",store,2, true);
        baskets = new ArrayList<>(sc.getBaskets());
        assertTrue(baskets.size()==1&&baskets.get(0).getProductItems().size()==1&&baskets.get(0).getProductItems().get(0).getProduct()==p&&baskets.get(0).getProductItems().get(0).getAmount()==3);
    }

    @Test
    public void view() throws SQLException {
        assertEquals("Your ShoppingCart details: \nempty!",sc.view());
        Product p = new Product("Shirt",null,null,40.0,"Fox",1);
        PersistenceController.create(p);
        User user1 = new User();
        user1.setUsername("tester1");
        StoreOwning storeOwning = new StoreOwning("Fox", "tester1");
        Store store = new Store("Fox",null,user1,storeOwning);
        store.getInventory().put(p,10);
        sc.addProduct("Shirt",store,1, true);
//        assertEquals("Your ShoppingCart details: \n"+(new ArrayList<>(sc.getBaskets())).get(0).viewBasket(),sc.view());

        assertEquals("[{\"amount\":1,\"price\":40.0,\"name\":\"Shirt\",\"store\":\"Fox\"}]",sc.view());
    }

    @Test
    public void edit() throws SQLException {
        Product p = new Product("Shirt",null,null,40.0,"Fox",1);
        PersistenceController.create(p);
        User user1 = new User();
        user1.setUsername("tester1");
        StoreOwning storeOwning = new StoreOwning("Fox", "tester1");
        Store store = new Store("Fox",null,user1,storeOwning);
        store.getInventory().put(p,10);
        sc.addProduct("Shirt",store,1, true);
        sc.edit(store,"Shirt",2, true);
        assertEquals(2,(new ArrayList<>(sc.getBaskets())).get(0).getProductItems().get(0).getAmount());
//        sc.edit(store,"Dress",2, true);
//        assertEquals(1,(new ArrayList<>(sc.getBaskets())).get(0).getProductItems().size());
        sc.edit(store,"Shirt",0, true);
        assertEquals(0,sc.getBaskets().size());
    }


}