package AcceptanceTests;

import ServiceLayer.SessionHandler;
import ServiceLayer.ShoppingCartHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC2_6 {

    private ShoppingCartHandler handler;
    private UUID session_id;

    @Before
    public void setUp() throws Exception {
        handler = new ShoppingCartHandler();
        session_id = (new SessionHandler()).openNewSession();
    }

    @BeforeClass
    public void init() throws Exception {
        (new StoreHandler()).resetStores();
        (new UsersHandler()).register("shenhav", "toya");
        (new UsersHandler()).login(session_id, "shenhav", "toya", false);
        (new StoreHandler()).openNewStore(session_id, "FoxHome", "stuff for home");
        (new StoreHandler()).UpdateInventory(session_id, "FoxHome", "pillow", 25.0, "BeautyProducts", "beauty pillow", 1);

    }

    @AfterClass
    public static void clean() {
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @After
    public void tearDown() throws Exception {
        (new SessionHandler()).closeSession(session_id);
    }

    @Test
    public void valid() {
        String result = handler.AddToShoppingBasket(session_id, "FoxHome", "pillow", 1);
        assertEquals("Items have been added to basket", result);
    }

    @Test
    public void productIsNotAvailable() {
        String result = handler.AddToShoppingBasket(session_id, "FoxHome", "banana", 1);
        assertEquals("The product isn't available in the store with the requested amount", result);
    }

    @Test
    public void storeDoesNotExist() {
        String result = handler.AddToShoppingBasket(session_id, "Fox", "banana", 1);
        assertEquals("The store doesn't exist in the trading system", result);
    }

    @Test
    public void emptyInput() {
        String result = handler.AddToShoppingBasket(session_id, "", "pillow", 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", result);
        result = handler.AddToShoppingBasket(session_id, null, "pillow", 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", result);
        result = handler.AddToShoppingBasket(session_id, "FoxHome", "", 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", result);
        result = handler.AddToShoppingBasket(session_id, "FoxHome", null, 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", result);
    }

}
