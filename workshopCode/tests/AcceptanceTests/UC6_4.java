package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import ServiceLayer.ViewPurchaseHistoryHandler;
import org.junit.*;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UC6_4 {

    private static ViewPurchaseHistoryHandler viewHistoryHandler;
    private static UUID session_id;

    @BeforeClass
    public static void init() throws Exception {
        PersistenceController.initiate(false);
        session_id = (new SessionHandler()).openNewSession();
        viewHistoryHandler = new ViewPurchaseHistoryHandler();
        (new UsersHandler()).login(session_id, "Admin159", "951", true);
        (new UsersHandler()).register("toya", "555"); // to check history
        (new StoreHandler()).openNewStore(session_id, "KKW", "best makeup products"); // to check history
    }

    @AfterClass
    public static void clean() throws SQLException {
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
        (new SessionHandler()).closeSession(session_id);
    }

    @Test
    public void validStore() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfStoreAsAdmin(session_id, "KKW");
        assertEquals("[]", result);
    }

    @Test
    public void validUser() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin(session_id, "toya");
        assertEquals("[]", result);
    }


    @Test
    public void emptyInputStore() {
        try{
            String result = viewHistoryHandler.viewPurchaseHistoryOfStoreAsAdmin(session_id, "");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name", e.getMessage());
        }
        try{
            viewHistoryHandler.ViewPurchaseHistoryOfStore(session_id, null);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name", e.getMessage());
        }
    }


    @Test
    public void emptyInputUser() {
        try{
            String result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin(session_id, "");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username", e.getMessage());
        }
        try{
            viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin(session_id, null);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username", e.getMessage());
        }
    }


//    @Test
//    public void notAdminMode() {
//        (new UsersHandler()).logout(session_id);
//        (new UsersHandler()).register("nufi", "1234");
//        (new UsersHandler()).login(session_id, "nufi", "1234", false);
//
//        try{
//            String result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin(session_id, "toya");
//            fail();
//        }catch(Exception e) {
//            assertEquals("Only admin user can view other users' purchase history", e.getMessage());
//        }
//        try{
//            viewHistoryHandler.viewPurchaseHistoryOfStoreAsAdmin(session_id, "KKW");
//            fail();
//        }catch(Exception e) {
//            assertEquals("Only admin user can view store's purchase history", e.getMessage());
//        }
//
//        (new UsersHandler()).logout(session_id);
//        (new UsersHandler()).login(session_id, "Admin159", "951", true);
//    }

    @Test
    public void storeExists() {
        try{
            String result = viewHistoryHandler.viewPurchaseHistoryOfStoreAsAdmin(session_id, "KYLIE");
            fail();
        }catch(Exception e) {
            assertEquals("The store requested doesn't exist in the system", e.getMessage());
        }
    }

    @Test
    public void userExists() {
        try{
            String result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin(session_id, "Kooper");
            fail();
        }catch(Exception e) {
            assertEquals("The user requested doesn't exist in the system", e.getMessage());
        }
    }

}
