package AcceptanceTests;

import ServiceLayer.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UC4_10 {

    private static ViewPurchaseHistoryHandler storePurchaseHistory;

    @Before
    public void setUp(){
        storePurchaseHistory = new ViewPurchaseHistoryHandler();
    }

    @BeforeClass
    public static void init(){
        (new UsersHandler()).register("noy", "1234");
        (new UsersHandler()).register("rachel", "1234");
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).register("zuzu", "1234");
        (new UsersHandler()).login("noy", "1234", false);
        (new StoreHandler()).openNewStore("Lalin", "beauty products");
        (new StoreHandler()).UpdateInventory("Lalin", "Body Cream ocean", 40.0, "BeautyProducts", "Velvety and soft skin lotion with ocean scent", 50);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub musk", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 20);
        (new StoreManagerHandler()).addStoreManager("rachel", "Lalin");
        (new StoreManagerHandler()).addStoreManager("maor", "Lalin");

        List<String> p = new LinkedList<>();
        p.add("View store purchase history");
        (new StoreManagerHandler()).editManagerPermissions("rachel", p, "Lalin");

        (new ShoppingCartHandler()).AddToShoppingBasket("Lalin", "Body Cream ocean", 5);
        (new ShoppingCartHandler()).purchaseCart();
    }


    @AfterClass
    public static void clean() {
        (new UsersHandler()).logout();
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Test
    public void valid() throws ParseException {
        //noy - store owner
        String result = storePurchaseHistory.ViewPurchaseHistoryOfStore("Lalin");
        JSONParser parser = new JSONParser();
        String expected = "[[{\"name\":\"Body Cream ocean\", \"price\":40.0, \"store\":\"Lalin\", \"amount\":5}]]";
        assertEquals(parser.parse(expected), parser.parse(result));

        //rachel - store manager with the required permissions
        (new UsersHandler()).logout();
        (new UsersHandler()).login("rachel", "1234", false);
        result = storePurchaseHistory.ViewPurchaseHistoryOfStore("Lalin");
        assertEquals(parser.parse(expected), parser.parse(result));
    }

    @Test
    public void storeDoesntExists() {
        try{
            storePurchaseHistory.ViewPurchaseHistoryOfStore("Swear");
        }
        catch(Exception e){
            assertEquals("This store doesn't exist", e.getMessage());
        }
    }

    @Test
    public void noPermissions() {
        //zuzu - neither a manager nor a store owner
        (new UsersHandler()).logout();
        (new UsersHandler()).login("zuzu", "1234", false);
        try{
            storePurchaseHistory.ViewPurchaseHistoryOfStore("Lalin");
        }
        catch(Exception e){
            assertEquals("You are not allowed to view this store's purchasing history", e.getMessage());
        }

        //maor - manager without required permission
        (new UsersHandler()).logout();
        (new UsersHandler()).login("maor", "1234", false);
        try{
            storePurchaseHistory.ViewPurchaseHistoryOfStore("Lalin");
        }
        catch(Exception e){
            assertEquals("You are not allowed to view this store's purchasing history", e.getMessage());
        }
    }

    @Test
    public void emptyStore() {
        try{
             storePurchaseHistory.ViewPurchaseHistoryOfStore("");
        }
        catch(Exception e){
            assertEquals("Must enter store name", e.getMessage());
        }
    }

}
