package AcceptanceTests;

import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UC4_1 {

    private static StoreHandler handler;
    private static UUID session_id;


    @BeforeClass
    public static void init() throws Exception{
        session_id = (new SessionHandler()).openNewSession();
        handler = new StoreHandler();
        (new UsersHandler()).register("toya","toya");
        (new UsersHandler()).register("shenhav","toya");
    }

    @Before
    public void setUp(){
        (new UsersHandler()).login(session_id, "toya","toya", false);
        (new StoreHandler()).openNewStore(session_id, "Castro","clothing");
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).login(session_id, "shenhav","toya", false);
        (new StoreHandler()).openNewStore(session_id, "FoxHome","stuff for home");
        ///(new StoreHandler()).UpdateInventory(session_id, "FoxHome","pillow", 25.0, "BeautyProducts", "beauty pillow", 1);


    }


    @AfterClass
    public static void clean(){
        (new UsersHandler()).resetUsers();
        (new SessionHandler()).closeSession(session_id);
    }

    @After
    public void tearDown(){
        (new StoreHandler()).resetStores();
    }

    @Test
    public void valid(){
        String result = handler.UpdateInventory(session_id, "FoxHome", "pillow", 20.0, "Food", "beauty pillow", 1);
        assertEquals("{\"SUCCESS\":\"The product has been added\"}", result);
    }

    @Test
    public void productDoesNotExist(){
        handler.UpdateInventory(session_id, "FoxHome", "banana", 20.0, "Food", "beauty pillow", 1);
        String result = handler.UpdateInventory(session_id, "FoxHome", "banana", 20.0, "Food", "yellow food", 1);
        assertEquals("{\"SUCCESS\":\"The product has been updated\"}", result);
    }

    @Test
    public void storeDoesNotExist(){
        try{
            String result = handler.UpdateInventory(session_id, "Fox", "banana", 20.0, "Food", "yellow food", 1);
            fail();
        }catch(Exception e) {
            assertEquals("This store doesn't exist", e.getMessage());
        }
    }

    @Test
    public void doesNotHavePrivileges(){
        try{
            String result = handler.UpdateInventory(session_id, "Castro", "dress", 200.0, "Clothing", "evening dress", 1);
            fail();
        }catch(Exception e) {
            assertEquals("Must have editing privileges", e.getMessage());
        }
    }

    @Test
    public void emptyInput(){
        try{
            String result = handler.UpdateInventory(session_id, "", "banana", 20.0, "Food", "yellow food", 1);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name, and product info", e.getMessage());
        }
        try{
            handler.UpdateInventory(session_id, null, "banana", 20.0, "Food", "yellow food", 1);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name, and product info", e.getMessage());
        }
        try{
            handler.UpdateInventory(session_id, "FoxHome", "", 20.0, "Clothing", "yellow food", 1);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name, and product info", e.getMessage());
        }
    }
}
