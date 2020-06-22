package AcceptanceTests;


import DataAccessLayer.PersistenceController;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import ServiceLayer.ViewInfoHandler;
import netscape.javascript.JSObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.*;

import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class UC2_4 {

    private ViewInfoHandler viewInfo;
    private static UUID session_id;

    @Before
    public void setUp(){
        this.viewInfo = new ViewInfoHandler();
    }

    @BeforeClass
    public static void init(){
        PersistenceController.initiate(false);
        session_id = (new SessionHandler()).openNewSession();
        (new UsersHandler()).register("noy", "1234");
        (new UsersHandler()).login(session_id, "noy", "1234", false);
        (new StoreHandler()).openNewStore(session_id, "Lalin", "beauty products");
        (new StoreHandler()).UpdateInventory(session_id, "Lalin", "Body Cream ocean", 40.0, "BeautyProducts", "Velvety and soft skin lotion with ocean scent", 1);
        (new StoreHandler()).UpdateInventory(session_id, "Lalin", "Body Scrub musk", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 1);
    }


    @AfterClass
    public static void clean() {
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
        (new SessionHandler()).closeSession(session_id);
    }

    @Test
    public void valid() throws ParseException {
        String result = viewInfo.viewStoreinfo("Lalin");
        String expected1 = "{\"name\":\"Lalin\",\"description\":\"beauty products\",\"products\":[{\"price\":50.0,\"productName\":\"Body Scrub musk\"},{\"price\":40.0,\"productName\":\"Body Cream ocean\"}]}";
        String expected2 = "{\"name\":\"Lalin\",\"description\":\"beauty products\",\"products\":[{\"price\":40.0,\"productName\":\"Body Cream ocean\"},{\"price\":50.0,\"productName\":\"Body Scrub musk\"}]}";
        JSONParser parser = new JSONParser();

        assertTrue(parser.parse(expected1).equals(parser.parse(result))||parser.parse(expected2).equals(parser.parse(result)));

        result = viewInfo.viewProductInfo("Lalin", "Body Cream ocean");
        String expected = "{\"name\":\"Body Cream ocean\",\"description\":\"Velvety and soft skin lotion with ocean scent\",\"price\":40.0}";
        assertEquals(parser.parse(expected), parser.parse(result));
    }

    @Test
    public void storeDoesntExist(){
        try {
            String result = viewInfo.viewStoreinfo("Swear");
        }
        catch(Exception e){
            assertEquals("This store doesn't exist in this trading system", e.getMessage());
        }
    }

    @Test
    public void storeNameEmpty(){
        try {
            String result = viewInfo.viewStoreinfo("");
        }
        catch(Exception e){
            assertEquals("The store name is invalid", e.getMessage());
        }
    }

    @Test
    public void productDoesntExist(){
        try {
            String result = viewInfo.viewProductInfo("Body oil", "Lalin");
        }
        catch(Exception e){
            assertEquals("This product is not available for purchasing in this store", e.getMessage());
        }
    }

    @Test
    public void productNameEmpty(){
        try {
            String result = viewInfo.viewProductInfo("", "Lalin");
        }
        catch(Exception e){
            assertEquals("The product name is invalid", e.getMessage());
        }
    }
}