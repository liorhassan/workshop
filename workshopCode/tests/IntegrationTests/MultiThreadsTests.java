package IntegrationTests;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.ShoppingCartHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.StoreManagerHandler;
import ServiceLayer.UsersHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MultiThreadsTests {
    @BeforeClass
    public static void init(){
        PersistenceController.initiate(false);

        (new UsersHandler()).register("toya", "1234");
        UUID se = SystemFacade.getInstance().createNewSession();

        (new UsersHandler()).login(se,"toya", "1234", false);

        (new StoreHandler()).openNewStore(se,"ZARA", "clothing");
        (new StoreHandler()).UpdateInventory(se,"ZARA", "white T-shirt", 5.0, "Clothing", "white T-shirt for men", 3);
        (new StoreHandler()).UpdateInventory(se,"ZARA", "jeans skirt", 50.0, "Clothing", "mini jeans skirt for women", 50);
        (new StoreHandler()).UpdateInventory(se, "ZARA", "Michael Kors bag", 1000.0, "Clothing", "bag", 50);

        (new UsersHandler()).register("ben", "1234");
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).register("rachel", "1234");
        (new UsersHandler()).register("amit", "1234");
        (new StoreHandler()).addStoreOwner(se, "maor", "ZARA");

        (new UsersHandler()).logout(se);
    }

    @AfterClass
    public static void clean(){
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Test
    public void purchaseTest() throws InterruptedException {
        AtomicInteger counterSecc = new AtomicInteger();
        AtomicInteger counterFail = new AtomicInteger();
        Thread th1 = new Thread(()->{
            UUID se = SystemFacade.getInstance().createNewSession();
            (new UsersHandler()).login(se, "maor", "1234", false);
            try{
                (new ShoppingCartHandler()).AddToShoppingBasket(se, "ZARA", "white T-shirt", 2);
                (new ShoppingCartHandler()).purchaseCart(se);
                counterSecc.getAndIncrement();
            }
            catch (Exception e){
                assertEquals("There is currently no stock of 2 white T-shirt products", e.getMessage());
                counterFail.getAndIncrement();
            }
            (new UsersHandler()).logout(se);
        });

        Thread th2 = new Thread(()->{
            UUID se = SystemFacade.getInstance().createNewSession();
            (new UsersHandler()).login(se, "rachel", "1234", false);
            try{
                (new ShoppingCartHandler()).AddToShoppingBasket(se, "ZARA", "white T-shirt", 2);
                (new ShoppingCartHandler()).purchaseCart(se);
                counterSecc.getAndIncrement();
            }
            catch (Exception e){
                assertEquals("There is currently no stock of 2 white T-shirt products", e.getMessage());
                counterFail.getAndIncrement();
            }
            (new UsersHandler()).logout(se);

        });
        th1.start();
        th2.start();
        Thread.sleep(1000);

        assertTrue(counterSecc.get() ==1);
        assertTrue(counterFail.get() ==1);
        assertTrue(SystemFacade.getInstance().isProductAvailable("ZARA", "white T-shirt", 1));
        assertTrue(!SystemFacade.getInstance().isProductAvailable("ZARA", "white T-shirt",  2));

    }

    @Test
    public void openStoreTest() throws InterruptedException {
        AtomicInteger counterSecc = new AtomicInteger();
        AtomicInteger counterFail = new AtomicInteger();
        Thread th1 = new Thread(()->{
            UUID se = SystemFacade.getInstance().createNewSession();
            (new UsersHandler()).login(se, "maor", "1234", false);
            try{
                (new StoreHandler()).openNewStore(se,"TheThread","food");
                counterSecc.getAndIncrement();
            }
            catch (Exception e){
                assertEquals("Store name already exists, please choose a different one", e.getMessage());
                counterFail.getAndIncrement();
            }
            (new UsersHandler()).logout(se);
        });

        Thread th2 = new Thread(()->{
            UUID se = SystemFacade.getInstance().createNewSession();
            (new UsersHandler()).login(se, "rachel", "1234", false);
            try{
                (new StoreHandler()).openNewStore(se,"TheThread","food");
                counterSecc.getAndIncrement();
            }
            catch (Exception e){
                assertEquals("Store name already exists, please choose a different one", e.getMessage());
                counterFail.getAndIncrement();
            }
            (new UsersHandler()).logout(se);
        });
        th1.start();
        th2.start();
        Thread.sleep(1000);

        assertTrue(counterSecc.get() ==1);
        assertTrue(counterFail.get() ==1);

    }

    @Test
    public void addStoreManagerTest() throws InterruptedException {
        AtomicInteger counterSecc = new AtomicInteger();
        AtomicInteger counterFail = new AtomicInteger();
        Thread th1 = new Thread(()->{
            UUID se = SystemFacade.getInstance().createNewSession();
            (new UsersHandler()).login(se, "maor", "1234", false);
            try{
                (new StoreManagerHandler()).addStoreManager(se, "rachel", "ZARA");
                counterSecc.getAndIncrement();
            }
            catch (Exception e){
                assertEquals("This username is already one of the store's managers", e.getMessage());
                counterFail.getAndIncrement();
            }
            (new UsersHandler()).logout(se);
        });

        Thread th2 = new Thread(()->{
            UUID se = SystemFacade.getInstance().createNewSession();
            (new UsersHandler()).login(se, "toya", "1234", false);
            try{
                (new StoreManagerHandler()).addStoreManager(se, "rachel", "ZARA");
                counterSecc.getAndIncrement();
            }
            catch (Exception e){
                assertEquals("This username is already one of the store's managers", e.getMessage());
                counterFail.getAndIncrement();
            }
            (new UsersHandler()).logout(se);
        });
        th1.start();
        th2.start();
        Thread.sleep(1000);

        assertTrue(counterSecc.get() ==1);
        assertTrue(counterFail.get() ==1);
        assertTrue(SystemFacade.getInstance().checkIfUserIsManager("ZARA", "rachel"));

    }

    @Test
    public void addRevealDiscountTest() throws InterruptedException {
        AtomicInteger counterSecc = new AtomicInteger();
        AtomicInteger counterFail = new AtomicInteger();
        Thread th1 = new Thread(()->{
            UUID se = SystemFacade.getInstance().createNewSession();
            (new UsersHandler()).login(se, "maor", "1234", false);
            try{
                (new StoreHandler()).addDiscountRevealedProduct("ZARA","jeans skirt", 30);
                counterSecc.getAndIncrement();
            }
            catch (Exception e){
                assertEquals("Cant add the discount on this product", e.getMessage());
                counterFail.getAndIncrement();
            }
            (new UsersHandler()).logout(se);
        });

        Thread th2 = new Thread(()->{
            UUID se = SystemFacade.getInstance().createNewSession();
            (new UsersHandler()).login(se, "toya", "1234", false);
            try{
                (new StoreHandler()).addDiscountRevealedProduct("ZARA","jeans skirt", 20);
                counterSecc.getAndIncrement();
            }
            catch (Exception e){
                assertEquals("Cant add the discount on this product", e.getMessage());
                counterFail.getAndIncrement();
            }
            (new UsersHandler()).logout(se);
        });
        th1.start();
        th2.start();
        Thread.sleep(1000);

        assertTrue(counterSecc.get() ==1);
        assertTrue(counterFail.get() ==1);
        assertTrue(SystemFacade.getInstance().checkIfUserIsManager("ZARA", "rachel"));

    }
}
