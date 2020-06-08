package AcceptanceTests;

import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC3_2 {

    public static UUID session_id;
    @BeforeClass
    public static void init() {
        session_id = (new SessionHandler()).openNewSession();
        UC2_3.setUp();
        (new UsersHandler()).login(session_id, "toya", "1234", false);
    }

    @AfterClass
    public static void clean() {
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
        (new SessionHandler()).closeSession(session_id);
        UC2_3.clean();
    }



    @After
    public void tearDown() throws Exception {
        (new StoreHandler()).resetStores();
    }

    @Test
    public void valid() {
        String result = (new StoreHandler()).openNewStore(session_id, "KKW store", "best Kim Kardashian beauty products");
        assertEquals("{\"SUCCESS\":\"The new store is now open!\"}", result);
    }

    @Test
    public void emptyInput(){
        try{
            String result = (new StoreHandler()).openNewStore(session_id, "", "best Kim Kardashian beauty products");
        }
        catch (Exception e) {
            assertEquals("Must enter store name and description", e.getMessage());
        }
        try{
            (new StoreHandler()).openNewStore(session_id, null, "best Kim Kardashian beauty products");
        }
        catch (Exception e) {
            assertEquals("Must enter store name and description", e.getMessage());
        }
        try{
            (new StoreHandler()).openNewStore(session_id, "KKW store", "");
        }
        catch (Exception e) {
            assertEquals("Must enter store name and description", e.getMessage());
        }
        try{
            (new StoreHandler()).openNewStore(session_id, "KKW store", null);
        }
        catch (Exception e) {
            assertEquals("Must enter store name and description", e.getMessage());
        }
    }

    @Test
    public void storeAlreadyExist(){
        (new StoreHandler()).openNewStore(session_id, "KKW store", "best Kim Kardashian beauty products");
        try{
            String result = (new StoreHandler()).openNewStore(session_id, "KKW store", "best storeeeee");
        }
        catch (Exception e) {
            assertEquals("Store name already exists, please choose a different one", e.getMessage());
        }
    }
}
