package AcceptanceTests;

import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC4_1 {

    private StoreHandler handler;
    private UUID session_id;

    @Before
    public void setUp() throws Exception{
        session_id = (new SessionHandler()).openNewSession();
        handler = new StoreHandler();
    }

    @BeforeClass
    public  void init() throws Exception{
        (new UsersHandler()).register("toya","toya");
        (new UsersHandler()).login(session_id, "toya","toya", false);
        (new StoreHandler()).openNewStore(session_id, "Castro","clothing");
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).register("shenhav","toya");
        (new UsersHandler()).login(session_id, "shenhav","toya", false);
        (new StoreHandler()).openNewStore(session_id, "FoxHome","stuff for home");
        (new StoreHandler()).UpdateInventory(session_id, "FoxHome","pillow", 25.0, "BeautyProducts", "beauty pillow", 1);

    }

    @AfterClass
    public static void clean(){
        (new StoreHandler()).resetStores();
        (new UsersHandler()).resetUsers();
    }

    @After
    public void tearDown() throws Exception {
        (new SessionHandler()).closeSession(session_id);
    }

    @Test
    public void valid(){
        String result = handler.UpdateInventory(session_id, "FoxHome", "pillow", 20.0, "Food", "beauty pillow", 1);
        assertEquals("The product has been updated", result);
    }

    @Test
    public void productDoesNotExist(){
        String result = handler.UpdateInventory(session_id, "FoxHome", "banana", 20.0, "Food", "yellow food", 1);
        assertEquals("The product has been added", result);
    }

    @Test
    public void storeDoesNotExist(){
        String result = handler.UpdateInventory(session_id, "Fox", "banana", 20.0, "Food", "yellow food", 1);
        assertEquals("This store doesn't exist", result);
    }

    @Test
    public void doesNotHavePrivileges(){
        String result = handler.UpdateInventory(session_id, "Castro", "dress", 200.0, "Clothing", "evening dress", 1);
        assertEquals("Must have editing privileges", result);
    }

    @Test
    public void emptyInput(){
        String result = handler.UpdateInventory(session_id, "", "banana", 20.0, "Food", "yellow food", 1);
        assertEquals("Must enter store name, and product info", result);
        result = handler.UpdateInventory(session_id, null, "banana", 20.0, "Food", "yellow food", 1);
        assertEquals("Must enter store name, and product info", result);
        result = handler.UpdateInventory(session_id, "FoxHome", "", 20.0, "Clothing", "yellow food", 1);
        assertEquals("Must enter store name, and product info", result);
    }
}
