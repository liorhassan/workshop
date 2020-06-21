package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import ServiceLayer.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.*;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC4_10 {

    private static ViewPurchaseHistoryHandler storePurchaseHistory;
    private static UUID session_id;

    @BeforeClass
    public static void init(){
        PersistenceController.initiate();
        storePurchaseHistory = new ViewPurchaseHistoryHandler();
        session_id = (new SessionHandler()).openNewSession();
        (new UsersHandler()).register("noy", "1234");
        (new UsersHandler()).register("rachel", "1234");
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).register("zuzu", "1234");
        (new UsersHandler()).login(session_id, "noy", "1234", false);
        (new StoreHandler()).openNewStore(session_id, "Lalin", "beauty products");
        (new StoreHandler()).UpdateInventory(session_id, "Lalin", "Body Cream ocean", 40.0, "BeautyProducts", "Velvety and soft skin lotion with ocean scent", 50);
        (new StoreHandler()).UpdateInventory(session_id, "Lalin", "Body Scrub musk", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 20);
        (new StoreManagerHandler()).addStoreManager(session_id, "rachel", "Lalin");
        (new StoreManagerHandler()).addStoreManager(session_id, "maor", "Lalin");

        List<String> p = new LinkedList<>();
        p.add("View Purchasing History");
        (new StoreManagerHandler()).editManagerPermissions(session_id, "rachel", p, "Lalin");

        (new ShoppingCartHandler()).AddToShoppingBasket(session_id, "Lalin", "Body Cream ocean", 5);
        (new ShoppingCartHandler()).purchaseCart(session_id);
    }


    @AfterClass
    public static void clean() {
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
        (new SessionHandler()).closeSession(session_id);
    }


    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void valid() throws ParseException {
        //noy - store owner
        String result = storePurchaseHistory.ViewPurchaseHistoryOfStore(session_id, "Lalin");
        JSONParser parser = new JSONParser();
        String expected = "[[{\"name\":\"Body Cream ocean\", \"price\":40.0, \"store\":\"Lalin\", \"amount\":5}]]";
        assertEquals(parser.parse(expected), parser.parse(result));

        //rachel - store manager with the required permissions
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).login(session_id, "rachel", "1234", false);
        result = storePurchaseHistory.ViewPurchaseHistoryOfStore(session_id, "Lalin");
        assertEquals(parser.parse(expected), parser.parse(result));
    }

    @Test
    public void storeDoesntExists() {
        try{
            storePurchaseHistory.ViewPurchaseHistoryOfStore(session_id, "Swear");
        }
        catch(Exception e){
            assertEquals("This store doesn't exist", e.getMessage());
        }
    }

    @Test
    public void noPermissions() {
        //zuzu - neither a manager nor a store owner
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).login(session_id, "zuzu", "1234", false);
        try{
            storePurchaseHistory.ViewPurchaseHistoryOfStore(session_id, "Lalin");
        }
        catch(Exception e){
            assertEquals("You are not allowed to view this store's purchasing history", e.getMessage());
        }

        //maor - manager without required permission
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).login(session_id, "maor", "1234", false);
        try{
            storePurchaseHistory.ViewPurchaseHistoryOfStore(session_id, "Lalin");
        }
        catch(Exception e){
            assertEquals("You are not allowed to view this store's purchasing history", e.getMessage());
        }
    }

    @Test
    public void emptyStore() {
        try{
             storePurchaseHistory.ViewPurchaseHistoryOfStore(session_id, "");
        }
        catch(Exception e){
            assertEquals("Must enter store name", e.getMessage());
        }
    }

}
