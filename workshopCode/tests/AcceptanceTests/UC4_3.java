package AcceptanceTests;

import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import static org.junit.Assert.assertEquals;

public class UC4_3 {

    private StoreHandler storeHandler;

    @BeforeClass
    public static void init() throws Exception{

        // store owner (appointer)
        (new UsersHandler()).register("nufi", "1234");

        // subscribed user (appointee)
        (new UsersHandler()).register("tooti", "1234");

        (new UsersHandler()).login("nufi", "1234", false);
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
        (new UsersHandler()).resetUsers();
        storeHandler.resetStores();

        // store owner (appointer)
        (new UsersHandler()).register("nufi", "1234");

        // subscribed user (appointee)
        (new UsersHandler()).register("tooti", "1234");

        (new UsersHandler()).login("nufi", "1234", false);

        (new StoreHandler()).openNewStore("KKW", "best Kim Kardashian beauty products");
    }

    @Test
    public void valid() {
        String result = storeHandler.addStoreOwner("tooti", "KKW");
        assertEquals("Username has been added as one of the store owners successfully", result);
    }

    @Test
    public void emptyInput(){
        String result = storeHandler.addStoreOwner("", "KKW");
        assertEquals("Must enter username and store name", result);
         result = storeHandler.addStoreOwner("tooti", "");
        assertEquals("Must enter username and store name", result);
        result = storeHandler.addStoreOwner(null, "KKW");
        assertEquals("Must enter username and store name", result);
        result = storeHandler.addStoreOwner("tooti", null);
        assertEquals("Must enter username and store name", result);
    }

    @Test
    public void storeDoesNotExist(){
        String result = storeHandler.addStoreOwner("tooti", "poosh");
        assertEquals("This store doesn't exist", result);
    }

    @Test
    public void userDoesNotExist(){
        String result = storeHandler.addStoreOwner("tooton", "KKW");
        assertEquals("This username doesn't exist", result);
    }

    @Test
    public void appointerIsNotOwner() {
        SystemFacade.getInstance().logout();
        SystemFacade.getInstance().register("toya");
        SystemFacade.getInstance().login("toya", false);
        String result = storeHandler.addStoreOwner("tooti", "KKW");
        assertEquals("You must be this store owner for this action", result);
        SystemFacade.getInstance().logout();
        SystemFacade.getInstance().login("nufi", false);
    }

    @Test
    public void userAlreadyOwner() {
        String result = storeHandler.addStoreOwner("nufi", "KKW");
        assertEquals("This username is already one of the store's owners", result);
    }
}
