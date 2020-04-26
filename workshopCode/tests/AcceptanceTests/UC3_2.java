package AcceptanceTests;

import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import static org.junit.Assert.assertEquals;

public class UC3_2 {

    private StoreHandler storeHandler;
    private UsersHandler usersHandler;

    @BeforeClass
    public void init() throws Exception{
        usersHandler = new UsersHandler();
        usersHandler.register("nufi", "123456");
        usersHandler.login("nufi", "123456", false);
    }

    @AfterClass
    public void clean() {
        usersHandler.resetUsers();
    }

    @Before
    public void setUp() throws Exception {
        storeHandler = new StoreHandler();
    }

    @After
    public void tearDown() throws Exception {
        storeHandler.resetStores();
    }

    @Test
    public void valid() {
        String result = storeHandler.openNewStore("KKW store", "best Kim Kardashian beauty products");
        assertEquals("The new store is now open!", result);
    }

    @Test
    public void emptyInput(){
        String result = storeHandler.openNewStore("", "best Kim Kardashian beauty products");
        assertEquals("Must enter store name and description", result);
        result = storeHandler.openNewStore(null, "best Kim Kardashian beauty products");
        assertEquals("Must enter store name and description", result);
        result = storeHandler.openNewStore("KKW store", "");
        assertEquals("Must enter store name and description", result);
        result = storeHandler.openNewStore("KKW store", null);
        assertEquals("Must enter store name and description", result);
    }

    @Test
    public void storeAlreadyExist(){
        storeHandler.openNewStore("KKW store", "best Kim Kardashian beauty products");
        String result = storeHandler.openNewStore("KKW store", "best storeeeee");
        assertEquals("Store name already exists, please choose a different one", result);
    }
}
