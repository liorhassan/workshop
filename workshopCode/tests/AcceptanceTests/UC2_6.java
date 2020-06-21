package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import ServiceLayer.SessionHandler;
import ServiceLayer.ShoppingCartHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UC2_6 {

    private static ShoppingCartHandler handler;
    private static UUID session_id;

    @BeforeClass
    public static void init() throws Exception {
        PersistenceController.initiate();
        session_id = (new SessionHandler()).openNewSession();
        handler = new ShoppingCartHandler();
        (new StoreHandler()).resetStores();
        (new UsersHandler()).register("shenhav", "toya");
        (new UsersHandler()).login(session_id, "shenhav", "toya", false);
        (new StoreHandler()).openNewStore(session_id, "FoxHome", "stuff for home");
        (new StoreHandler()).UpdateInventory(session_id, "FoxHome", "pillow", 25.0, "BeautyProducts", "beauty pillow", 1);

    }

    @AfterClass
    public static void clean() {
        (new SessionHandler()).closeSession(session_id);
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }


    @Test
    public void valid() {
        String result = handler.AddToShoppingBasket(session_id, "FoxHome", "pillow", 1);
        assertEquals("{\"SUCCESS\":\"Items have been added to basket\"}", result);
    }

    @Test
    public void productIsNotAvailable() {
        try{
            String result = handler.AddToShoppingBasket(session_id, "FoxHome", "banana", 1);
            fail();
        }catch(Exception e) {
            assertEquals("The product isn't available in the store with the requested amount", e.getMessage());
        }
    }

    @Test
    public void storeDoesNotExist() {
        try{
            String result = handler.AddToShoppingBasket(session_id, "Fox", "banana", 1);
            fail();
        }catch(Exception e) {
            assertEquals("The store doesn't exist in the trading system", e.getMessage());
        }
    }

    @Test
    public void emptyInput() {
        try{
            String result = handler.AddToShoppingBasket(session_id, "", "pillow", 1);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name and product name and amount bigger than 0", e.getMessage());
        }
        try{
            handler.AddToShoppingBasket(session_id, null, "pillow", 1);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name and product name and amount bigger than 0", e.getMessage());
        }
        try{
            handler.AddToShoppingBasket(session_id, "FoxHome", "", 1);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name and product name and amount bigger than 0", e.getMessage());
        }
        try{
            handler.AddToShoppingBasket(session_id, "FoxHome", null, 1);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name and product name and amount bigger than 0", e.getMessage());
        }
    }

}
