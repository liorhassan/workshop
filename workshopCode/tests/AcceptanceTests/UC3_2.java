package AcceptanceTests;

import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC3_2 {

    private UUID session_id;
    @BeforeClass
    public void init() {
        session_id = (new SessionHandler()).openNewSession();
        UC2_3.setUp();
        (new UsersHandler()).login(session_id, "toya", "1234", false);
    }

    @AfterClass
    public static void clean() {
        (new UsersHandler()).resetUsers();
    }



    @After
    public void tearDown() throws Exception {
        (new StoreHandler()).resetStores();
        (new SessionHandler()).closeSession(session_id);
    }

    @Test
    public void valid() {
        String result = (new StoreHandler()).openNewStore(session_id, "KKW store", "best Kim Kardashian beauty products");
        assertEquals("The new store is now open!", result);
    }

    @Test
    public void emptyInput(){
        String result = (new StoreHandler()).openNewStore(session_id, "", "best Kim Kardashian beauty products");
        assertEquals("Must enter store name and description", result);
        result = (new StoreHandler()).openNewStore(session_id, null, "best Kim Kardashian beauty products");
        assertEquals("Must enter store name and description", result);
        result = (new StoreHandler()).openNewStore(session_id, "KKW store", "");
        assertEquals("Must enter store name and description", result);
        result = (new StoreHandler()).openNewStore(session_id, "KKW store", null);
        assertEquals("Must enter store name and description", result);
    }

    @Test
    public void storeAlreadyExist(){
        (new StoreHandler()).openNewStore(session_id, "KKW store", "best Kim Kardashian beauty products");
        String result = (new StoreHandler()).openNewStore(session_id, "KKW store", "best storeeeee");
        assertEquals("Store name already exists, please choose a different one", result);
    }
}
