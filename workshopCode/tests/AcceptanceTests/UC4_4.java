package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.StoreManagerHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UC4_4 {

    private static StoreHandler storeHandler;
    private static UUID session_id;

    @BeforeClass
    public static void init() throws Exception{
        PersistenceController.initiate(false);
        session_id = (new SessionHandler()).openNewSession();
        storeHandler = new StoreHandler();

        // store owner (appointer)
        (new UsersHandler()).register("nufi", "1234");

        // subscribed user (appointee1)
        (new UsersHandler()).register("tooti", "1234");
        // subscribed user (appointee2)
        (new UsersHandler()).register("lior", "1234");

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
    public void valid1() {
        String result = storeHandler.addStoreOwner(session_id, "tooti", "KKW");
        //add the second owner - dont need approvement
        assertEquals("{\"SUCCESS\":\"the appointment of the new owner is done successfully\"}", result);
        String result1 = storeHandler.removeStoreOwner(session_id, "tooti", "KKW");
        assertEquals("owner been removed successfully, more appointments was deleted: ", result1);

    }
    @Test
    public void valid2() throws SQLException {
        UUID sessionId2 = (new SessionHandler()).openNewSession();
        (new UsersHandler()).login(sessionId2,"tooti","1234", false);
        String result = storeHandler.addStoreOwner(session_id, "tooti", "KKW");
        //add the second owner - dont need approvement
        assertEquals("{\"SUCCESS\":\"the appointment of the new owner is done successfully\"}", result);
        //tooti add new manager
        (new StoreManagerHandler()).addStoreManager(sessionId2,"lior", "KKW");

        String result1 = storeHandler.removeStoreOwner(session_id, "tooti", "KKW");
        assertEquals("owner been removed successfully, more appointments was deleted: lior-manager ", result1);
        (new UsersHandler()).logout(sessionId2);
    }



    @Test
    public void emptyInput(){
        storeHandler.addStoreOwner(session_id, "tooti", "KKW");
        try{
            storeHandler.removeStoreOwner(session_id, "", "KKW");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
        try{
            storeHandler.removeStoreOwner(session_id, "tooti", "");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
        try{
            storeHandler.removeStoreOwner(session_id, null, "KKW");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
        try{
            storeHandler.removeStoreOwner(session_id, "tooti", null);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
    }

    @Test
    public void storeDoesNotExist(){
        storeHandler.addStoreOwner(session_id, "tooti", "KKW");

        try{
            String result = storeHandler.removeStoreOwner(session_id, "tooti", "poosh");
            fail();
        }catch(Exception e) {
            assertEquals("This store doesn't exist", e.getMessage());
        }
    }

    @Test
    public void userDoesNotExist(){
        storeHandler.addStoreOwner(session_id, "tooti", "KKW");
        try{
            String result = storeHandler.removeStoreOwner(session_id, "tooton", "KKW");
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
            String result = storeHandler.removeStoreOwner(session_id, "tooti", "KKW");
            fail();
        }catch(Exception e) {
            assertEquals("You must be this store owner for this command", e.getMessage());
        }
        SystemFacade.getInstance().logout(session_id);
        SystemFacade.getInstance().login(session_id, "nufi", false);
    }

}
