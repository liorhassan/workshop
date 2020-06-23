package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.StoreManagerHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UC4_7 {
    private StoreManagerHandler handler;
    private static UUID session_id;

    @Before
    public void setUp() throws Exception{
        handler = new StoreManagerHandler();
    }

    @BeforeClass
    public static void init() throws Exception{
        PersistenceController.initiate(false);
        session_id = (new SessionHandler()).openNewSession();
        (new UsersHandler()).register("tester","tester");
        (new UsersHandler()).login(session_id, "tester","tester", false);
        (new StoreHandler()).openNewStore(session_id, "Castro", "clothing");
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).register("shauli","shauli");
        (new UsersHandler()).register("toya","toya");
        (new UsersHandler()).login(session_id, "shauli","shauli", false);
        (new StoreHandler()).openNewStore(session_id, "FoxHome", "stuff for home");
        (new StoreManagerHandler()).addStoreManager(session_id, "toya","FoxHome");
    }

    @AfterClass
    public static void clean() throws SQLException {
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
        (new SessionHandler()).closeSession(session_id);
    }

    @Test
    public void valid(){
        String result = handler.removeStoreManager(session_id, "toya","FoxHome");
        assertEquals("{\"SUCCESS\":\"Manager removed successfully!\"}", result);
        (new StoreManagerHandler()).addStoreManager(session_id, "toya","FoxHome");
    }

    @Test
    public void storeDoesNotExists(){
        try{
            String result = handler.removeStoreManager(session_id, "toya","Fox");
            fail();
        }catch(Exception e) {
            assertEquals("This store doesn't exist", e.getMessage());
        }
    }

    @Test
    public void userDoesNotExists(){
        try{
            String result = handler.removeStoreManager(session_id, "cooper","FoxHome");
            fail();
        }catch(Exception e) {
            assertEquals("This username doesn't exist", e.getMessage());
        }
    }

//    @Test
//    public void doesNotHavePrivileges(){
//        try{
//            String result = handler.removeStoreManager(session_id, "toya", "Castro");
//            fail();
//        }catch(Exception e) {
//            assertEquals("You must be this store owner for this command", e.getMessage());
//        }
//    }

    @Test
    public void notAppointedByUser(){
        try{
            String result = handler.removeStoreManager(session_id, "shauli","FoxHome");
            fail();
        }catch(Exception e) {
            assertEquals("This username is not one of this store's managers appointed by you", e.getMessage());
        }
    }

    @Test
    public void emptyInput(){
        try{
            String result = handler.removeStoreManager(session_id, "","FoxHome");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
        try{
        handler.removeStoreManager(session_id, null,"FoxHome");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
        try{
        handler.removeStoreManager(session_id, "toya","");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
        try{
            handler.removeStoreManager(session_id, "toya",null);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
    }

}
