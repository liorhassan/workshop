package AcceptanceTests;

import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import ServiceLayer.ViewPurchaseHistoryHandler;
import org.junit.*;

import static org.junit.Assert.assertEquals;

public class UC6_4 {

    private ViewPurchaseHistoryHandler viewHistoryHandler;

    @BeforeClass
    public static void init() throws Exception {
        (new UsersHandler()).login("Admin159", "951", true);
        (new UsersHandler()).register("toya", "555"); // to check history
        (new StoreHandler()).openNewStore("KKW", "best makeup products"); // to check history
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
    }

    @Test
    public void validStore() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfStoreAsAdmin("KKW");
        assertEquals("Shopping history of the store:" + "\n", result);
    }

    @Test
    public void validUser() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin("toya");
        assertEquals("Shopping history:" + "\n", result);
    }


    @Test
    public void emptyInputStore() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfStoreAsAdmin("");
        assertEquals("Must enter store name", result);
        result = viewHistoryHandler.ViewPurchaseHistoryOfStore(null);
        assertEquals("Must enter store name", result);
    }


    @Test
    public void emptyInputUser() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin("");
        assertEquals("Must enter username", result);
        result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin(null);
        assertEquals("Must enter username", result);
    }


    @Test
    public void notAdminMode() {
        (new UsersHandler()).logout();
        (new UsersHandler()).register("nufi", "1234");
        (new UsersHandler()).login("nufi", "1234", false);

        String result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin("toya");
        assertEquals("Only admin user can view other users' purchase history", result);
        result = viewHistoryHandler.viewPurchaseHistoryOfStoreAsAdmin("KKW");
        assertEquals("Only admin user can view store's purchase history", result);

        (new UsersHandler()).logout();
        (new UsersHandler()).login("Admin159", "951", true);
    }

    @Test
    public void storeExists() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfStoreAsAdmin("KYLIE");
        assertEquals("The store requested doesn't exist in the system", result);
    }

    @Test
    public void userExists() {
        String result = viewHistoryHandler.viewPurchaseHistoryOfUserAsAdmin("Kooper");
        assertEquals("The user requested doesn't exist in the system", result);
    }

}
