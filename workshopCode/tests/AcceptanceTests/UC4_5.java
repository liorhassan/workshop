package AcceptanceTests;
import ServiceLayer.UsersHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.StoreManagerHandler;
import org.junit.*;
import static org.junit.Assert.assertEquals;

public class UC4_5 {
    private StoreManagerHandler service;

    @Before
    public void setUp() throws Exception{
        service = new StoreManagerHandler();
    }

    @BeforeClass
    public static void init() throws Exception{
        (new UsersHandler()).register("lior", "1234");
        (new UsersHandler()).register("Aviv", "good");
        (new UsersHandler()).register("Amit", "good");
        (new UsersHandler()).login("Amit", "good", false);
        (new StoreHandler()).openNewStore("Shufersal", "SuperMarket");
        (new UsersHandler()).logout();
        (new UsersHandler()).login("lior", "1234", false);
        (new StoreHandler()).openNewStore("Rami Levi", "SuperMarket");
    }

    @Test
    public void successful(){
        String output1 = service.addStoreManager("Aviv", "Rami Levi");
        assertEquals("Username has been added as one of the store managers successfully", output1);
        service.removeStoreManager("Aviv","Rami Levi");
    }

    @Test
    public void storeDoesNotExists(){
        String output1 = service.addStoreManager("Aviv","Osher Ad");
        assertEquals("This store doesn't exist", output1);
    }

    @Test
    public void userDoesNotExists(){
        String output = service.addStoreManager("Ben", "Rami Levi");
        assertEquals("This username doesn't exist", output);
    }

    @Test
    public void doesNotAStoreOwner(){
        String output = service.addStoreManager("Aviv", "Shufersal");
        assertEquals("You must be a store owner for this action", output);
    }

    @Test
    public void alreadyAManager(){
        String output1 = service.addStoreManager("Aviv", "Rami Levi");
        assertEquals("Username has been added as one of the store managers successfully", output1);
        String output2 = service.addStoreManager("Aviv", "Rami Levi");
        assertEquals("This username is already one of the store's managers", output2);
    }

    @Test
    public void emptyInput(){
        String output1 = service.addStoreManager("","Rami Levi");
        assertEquals("Must enter username and store name", output1);
        String output2 = service.addStoreManager(null,"Rami Levi");
        assertEquals("Must enter username and store name", output2);
        String output3 = service.addStoreManager("Aviv","");
        assertEquals("Must enter username and store name", output3);
        String output4 = service.addStoreManager("Aviv",null);
        assertEquals("Must enter username and store name", output4);
    }

}
