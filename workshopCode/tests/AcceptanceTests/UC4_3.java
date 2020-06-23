package AcceptanceTests;

import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UC4_3 {

    private static StoreHandler storeHandler;
    private static UUID session_id;

    @BeforeClass
    public static void init() throws Exception{
        session_id = (new SessionHandler()).openNewSession();
        storeHandler = new StoreHandler();

        // store owner (appointer)
        (new UsersHandler()).register("nufi", "1234");

        // subscribed user (appointee)
        (new UsersHandler()).register("tooti", "1234");

        (new UsersHandler()).login(session_id, "nufi", "1234", false);

        (new StoreHandler()).openNewStore(session_id, "KKW", "best Kim Kardashian beauty products");

    }

    @AfterClass
    public static void clean() throws SQLException {
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
        (new SessionHandler()).closeSession(session_id);
    }

    @After
    public void tearDown() throws Exception {
        storeHandler.resetStores();
        (new StoreHandler()).openNewStore(session_id, "KKW", "best Kim Kardashian beauty products");
    }

    @Test
    public void valid() {
        String result = storeHandler.addStoreOwner(session_id, "tooti", "KKW");
        assertEquals("{\"SUCCESS\":\"Username has been added as one of the store owners successfully\"}", result);
    }

    @Test
    public void emptyInput(){
        try{
            String result = storeHandler.addStoreOwner(session_id, "", "KKW");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
        try{
            storeHandler.addStoreOwner(session_id, "tooti", "");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
        try{
            storeHandler.addStoreOwner(session_id, null, "KKW");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
        try{
            storeHandler.addStoreOwner(session_id, "tooti", null);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
    }

    @Test
    public void storeDoesNotExist(){
        try{
            String result = storeHandler.addStoreOwner(session_id, "tooti", "poosh");
            fail();
        }catch(Exception e) {
            assertEquals("This store doesn't exist", e.getMessage());
        }
    }

    @Test
    public void userDoesNotExist(){
        try{
            String result = storeHandler.addStoreOwner(session_id, "tooton", "KKW");
            fail();
        }catch(Exception e) {
            assertEquals("This username doesn't exist", e.getMessage());
        }
    }

    @Test
    public void appointerIsNotOwner() throws SQLException {
        SystemFacade.getInstance().logout(session_id);
        SystemFacade.getInstance().register("toya");
        SystemFacade.getInstance().login(session_id, "toya", false);
        try{
            String result = storeHandler.addStoreOwner(session_id, "tooti", "KKW");
            fail();
        }catch(Exception e) {
            assertEquals("You must be this store owner for this action", e.getMessage());
        }
        SystemFacade.getInstance().logout(session_id);
        SystemFacade.getInstance().login(session_id, "nufi", false);
    }

    @Test
    public void userAlreadyOwner() {
        try{
            String result = storeHandler.addStoreOwner(session_id, "nufi", "KKW");
            fail();
        }catch(Exception e) {
            assertEquals("This username is already one of the store's owners", e.getMessage());
        }
    }
}
