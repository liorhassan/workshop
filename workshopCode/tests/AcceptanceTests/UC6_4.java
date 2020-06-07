package AcceptanceTests;

import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import ServiceLayer.ViewPurchaseHistoryHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC6_4 {

    private ViewPurchaseHistoryHandler viewHistoryHandler;
    private static UUID session_id;

    @BeforeClass
    public static void init() throws Exception {
        session_id = (new SessionHandler()).openNewSession();
        (new UsersHandler()).login(session_id, "Admin159", "951", true);
        (new UsersHandler()).register("toya", "555"); // to check history
        (new StoreHandler()).openNewStore(session_id, "KKW", "best makeup products"); // to check history
    }

    @AfterClass
    public static void clean() {
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Before
    public void setUp() {
        viewHistoryHandler = new ViewPurchaseHistoryHandler();
    }


    @After
    public void tearDown() throws Exception {
        (new SessionHandler()).closeSession(session_id);
    }

    @Test
    public void validStore() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfStoreAsAdmin(session_id, "KKW");
        assertEquals("Shopping history of the store:" + "\n", result);
    }

    @Test
    public void validUser() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin(session_id, "toya");
        assertEquals("Shopping history:" + "\n", result);
    }


    @Test
    public void emptyInputStore() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfStoreAsAdmin(session_id, "");
        assertEquals("Must enter store name", result);
        result = viewHistoryHandler.ViewPurchaseHistoryOfStore(session_id, null);
        assertEquals("Must enter store name", result);
    }


    @Test
    public void emptyInputUser() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin(session_id, "");
        assertEquals("Must enter username", result);
        result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin(session_id, null);
        assertEquals("Must enter username", result);
    }


    @Test
    public void notAdminMode() {
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).register("nufi", "1234");
        (new UsersHandler()).login(session_id, "nufi", "1234", false);

        String result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin(session_id, "toya");
        assertEquals("Only admin user can view other users' purchase history", result);
        result = viewHistoryHandler.viewPurchaseHistoryOfStoreAsAdmin(session_id, "KKW");
        assertEquals("Only admin user can view store's purchase history", result);

        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).login(session_id, "Admin159", "951", true);
    }

    @Test
    public void storeExists() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfStoreAsAdmin(session_id, "KYLIE");
        assertEquals("The store requested doesn't exist in the system", result);
    }

    @Test
    public void userExists() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin(session_id, "Kooper");
        assertEquals("The user requested doesn't exist in the system", result);
    }

}
