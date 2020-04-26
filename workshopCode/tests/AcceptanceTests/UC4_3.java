package AcceptanceTests;

import DomainLayer.SystemHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import static org.junit.Assert.assertEquals;

public class UC4_3 {

    private StoreHandler storeHandler;

    @BeforeClass
    public static void init() throws Exception{

        // store owner (appointer)
        (new UsersHandler()).register("nufi", "123456");

        // subscribed user (appointee)
        (new UsersHandler()).register("tooti", "9999");

        (new UsersHandler()).login("nufi", "123456", false);
        (new StoreHandler()).openNewStore("KKW", "best Kim Kardashian beauty products");
    }

    @AfterClass
    public static void clean() {
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Before
    public void setUp() throws Exception {
        storeHandler = new StoreHandler();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void valid() {
        String result = storeHandler.openNewStore("tooti", "KKW");
        assertEquals("Username has been added as one of the store owners successfully", result);
    }

    @Test
    public void emptyInput(){
        String result = storeHandler.openNewStore("", "KKW");
        assertEquals("Must enter username and store name", result);
        result = storeHandler.openNewStore(null, "KKW");
        assertEquals("Must enter username and store name", result);
        result = storeHandler.openNewStore("tooti", "");
        assertEquals("Must enter username and store name", result);
        result = storeHandler.openNewStore("tooti", null);
        assertEquals("Must enter username and store name", result);
    }

    @Test
    public void storeDoesNotExist(){
        String result = storeHandler.openNewStore("tooti", "poosh");
        assertEquals("This store doesn't exist", result);
    }

    @Test
    public void userDoesNotExist(){
        String result = storeHandler.openNewStore("tooton", "KKW");
        assertEquals("This username doesn't exist", result);
    }

    @Test
    public void appointerIsNotOwner() {
        SystemHandler.getInstance().logout();
        SystemHandler.getInstance().register("toya");
        SystemHandler.getInstance().login("toya", false);
        String result = storeHandler.openNewStore("tooti", "KKW");
        assertEquals("You must be this store owner for this action", result);
        SystemHandler.getInstance().logout();
        SystemHandler.getInstance().login("nufi", false);
    }

    @Test
    public void userAlreadyOwner() {
        SystemHandler.getInstance().logout();
        SystemHandler.getInstance().register("cooper");
        SystemHandler.getInstance().login("nufi", false);
        storeHandler.openNewStore("cooper", "KKW");
        String result = storeHandler.openNewStore("cooper", "KKW");
        assertEquals("This username is already one of the store's owners", result);
    }
}
