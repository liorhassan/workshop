package AcceptanceTests;


import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import ServiceLayer.ViewInfoHandler;
import netscape.javascript.JSObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class UC2_4 {

    private ViewInfoHandler viewInfo;

    @Before
    public void setUp(){
        this.viewInfo = new ViewInfoHandler();
    }

    @BeforeClass
    public static void init(){
        (new UsersHandler()).register("noy", "1234");
        (new UsersHandler()).login("noy", "1234", false);
        (new StoreHandler()).openNewStore("Lalin", "beauty products");
        (new StoreHandler()).UpdateInventory("Lalin", "Body Cream ocean", 40.0, "BeautyProducts", "Velvety and soft skin lotion with ocean scent", 1);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub musk", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 1);
    }


    @AfterClass
    public static void clean() {
        (new UsersHandler()).logout();
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Test
    public void valid() throws ParseException {
        String result = viewInfo.viewStoreinfo("Lalin");
        String expected = "{\"name\":\"Lalin\",\"description\":\"beauty products\",\"products\":[{\"price\":40.0,\"productName\":\"Body Cream ocean\"},{\"price\":50.0,\"productName\":\"Body Scrub musk\"}]}";
        JSONParser parser = new JSONParser();

        assertEquals(parser.parse(expected), parser.parse(result));

        result = viewInfo.viewProductInfo("Lalin", "Body Cream ocean");
        expected = "{\"name\":\"Body Cream ocean\",\"description\":\"Velvety and soft skin lotion with ocean scent\",\"price\":40.0}";
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