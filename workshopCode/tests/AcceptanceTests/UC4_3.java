package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UC4_3 {

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
    public static void clean() {
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
        UUID sessionId2 = (new SessionHandler()).openNewSession();
        (new UsersHandler()).login(sessionId2,"tooti","1234", false);
        String result = storeHandler.addStoreOwner(session_id, "tooti", "KKW");
        //add the second owner - dont need approvement
        assertEquals("{\"SUCCESS\":\"the appointment of the new owner is done successfully\"}", result);
        //declined
        String result2 = storeHandler.addStoreOwner(session_id,"lior","KKW");
        assertEquals("{\"SUCCESS\":\"the appointment of the new owner is waiting for the owners response\"}", result2);
        String result3 = storeHandler.responseToAppointmentRequest(sessionId2,"lior","KKW",false);
        assertEquals("{\"SUCCESS\":\"your response was updated successfully - the new appointment declined\"}", result3);
     (new UsersHandler()).logout(sessionId2);

    }

    @Test
    public void valid2() {
        UUID sessionId2 = (new SessionHandler()).openNewSession();
        (new UsersHandler()).login(sessionId2,"tooti","1234", false);
        String result = storeHandler.addStoreOwner(session_id, "tooti", "KKW");
        //add the second owner - dont need approvement
        assertEquals("{\"SUCCESS\":\"the appointment of the new owner is done successfully\"}", result);
      //approved
        String result4 = storeHandler.addStoreOwner(session_id,"lior","KKW");
        assertEquals("{\"SUCCESS\":\"the appointment of the new owner is waiting for the owners response\"}", result4);
        String result5 = storeHandler.responseToAppointmentRequest(sessionId2,"lior", "KKW",true);
        assertEquals("{\"SUCCESS\":\"your response was updated successfully - the new appointment approved\"}", result5);
        (new UsersHandler()).logout(sessionId2);

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
    public void appointerIsNotOwner() {
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
