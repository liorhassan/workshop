package AcceptanceTests;

import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC4_3 {

    private StoreHandler storeHandler;
    private UUID session_id;

    @BeforeClass
    public void init() throws Exception{

        // store owner (appointer)
        (new UsersHandler()).register("nufi", "1234");

        // subscribed user (appointee)
        (new UsersHandler()).register("tooti", "1234");

        (new UsersHandler()).login(session_id, "nufi", "1234", false);
        (new StoreHandler()).openNewStore(session_id, "KKW", "best Kim Kardashian beauty products");
    }

    @AfterClass
    public static void clean() {
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Before
    public void setUp() throws Exception {
        session_id = (new SessionHandler()).openNewSession();
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

        (new UsersHandler()).login(session_id, "nufi", "1234", false);

        (new StoreHandler()).openNewStore(session_id, "KKW", "best Kim Kardashian beauty products");
        (new SessionHandler()).closeSession(session_id);
    }

    @Test
    public void valid() {
        String result = storeHandler.addStoreOwner(session_id, "tooti", "KKW");
        assertEquals("Username has been added as one of the store owners successfully", result);
    }

    @Test
    public void emptyInput(){
        String result = storeHandler.addStoreOwner(session_id, "", "KKW");
        assertEquals("Must enter username and store name", result);
         result = storeHandler.addStoreOwner(session_id, "tooti", "");
        assertEquals("Must enter username and store name", result);
        result = storeHandler.addStoreOwner(session_id, null, "KKW");
        assertEquals("Must enter username and store name", result);
        result = storeHandler.addStoreOwner(session_id, "tooti", null);
        assertEquals("Must enter username and store name", result);
    }

    @Test
    public void storeDoesNotExist(){
        String result = storeHandler.addStoreOwner(session_id, "tooti", "poosh");
        assertEquals("This store doesn't exist", result);
    }

    @Test
    public void userDoesNotExist(){
        String result = storeHandler.addStoreOwner(session_id, "tooton", "KKW");
        assertEquals("This username doesn't exist", result);
    }

    @Test
    public void appointerIsNotOwner() {
        SystemFacade.getInstance().logout(session_id);
        SystemFacade.getInstance().register("toya");
        SystemFacade.getInstance().login(session_id, "toya", false);
        String result = storeHandler.addStoreOwner(session_id, "tooti", "KKW");
        assertEquals("You must be this store owner for this action", result);
        SystemFacade.getInstance().logout(session_id);
        SystemFacade.getInstance().login(session_id, "nufi", false);
    }

    @Test
    public void userAlreadyOwner() {
        String result = storeHandler.addStoreOwner(session_id, "nufi", "KKW");
        assertEquals("This username is already one of the store's owners", result);
    }
}
