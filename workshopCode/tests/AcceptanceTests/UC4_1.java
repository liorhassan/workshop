package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UC4_1 {

    private static StoreHandler handler;
    private static UUID session_id;


    @BeforeClass
    public static void init() throws Exception{
        PersistenceController.initiate(false);
        session_id = (new SessionHandler()).openNewSession();
        handler = new StoreHandler();
        (new UsersHandler()).register("ben","toya");
        (new UsersHandler()).register("liora","toya");
        (new UsersHandler()).login(session_id, "ben","toya", false);
        (new StoreHandler()).openNewStore(session_id, "BBB","food");
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).login(session_id, "liora","toya", false);
        (new StoreHandler()).openNewStore(session_id, "BurgerRoom","food");
    }


    @AfterClass
    public static void clean() throws SQLException {
        (new UsersHandler()).resetUsers();
        (new SessionHandler()).closeSession(session_id);
    }

    @After
    public void tearDown() throws SQLException {
        (new StoreHandler()).resetStores();
    }

    @Test
    public void valid(){
        String result = handler.UpdateInventory(session_id, "BurgerRoom", "pillow", 20.0, "Food", "beauty pillow", 1);
        assertEquals("{\"SUCCESS\":\"The product has been added\"}", result);
    }

    @Test
    public void productDoesNotExist(){
        handler.UpdateInventory(session_id, "BurgerRoom", "banana", 20.0, "Food", "beauty pillow", 1);
        String result = handler.UpdateInventory(session_id, "BurgerRoom", "banana", 20.0, "Food", "yellow food", 1);
        assertEquals("{\"SUCCESS\":\"The product has been updated\"}", result);
    }

    @Test
    public void storeDoesNotExist(){
        try{
            String result = handler.UpdateInventory(session_id, "BurgersBar", "banana", 20.0, "Food", "yellow food", 1);
            fail();
        }catch(Exception e) {
            assertEquals("This store doesn't exist", e.getMessage());
        }
    }

    @Test
    public void doesNotHavePrivileges(){
        try{
            String result = handler.UpdateInventory(session_id, "BBB", "dress", 200.0, "Clothing", "evening dress", 1);
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
            handler.UpdateInventory(session_id, "BurgerRoom", "", 20.0, "Clothing", "yellow food", 1);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name, and product info", e.getMessage());
        }
    }
}
