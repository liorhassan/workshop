package AcceptanceTests;
import DataAccessLayer.PersistenceController;
import ServiceLayer.SessionHandler;
import ServiceLayer.UsersHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.StoreManagerHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UC4_5 {
    private static StoreManagerHandler service;
    private static UUID session_id;


    @BeforeClass
    public static void init() throws Exception{
        PersistenceController.initiate();
        session_id = (new SessionHandler()).openNewSession();
        service = new StoreManagerHandler();
        (new UsersHandler()).register("lior", "1234");
        (new UsersHandler()).register("Aviv", "good");
        (new UsersHandler()).register("Amit", "good");
        (new UsersHandler()).login(session_id, "Amit", "good", false);
        (new StoreHandler()).openNewStore(session_id, "Shufersal", "SuperMarket");
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).login(session_id, "lior", "1234", false);
        (new StoreHandler()).openNewStore(session_id, "Rami Levi", "SuperMarket");
    }

    @AfterClass
    public static void clean() throws Exception {
        (new SessionHandler()).closeSession(session_id);
    }

    @Test
    public void successful(){
        String output1 = service.addStoreManager(session_id, "Aviv", "Rami Levi");
        assertEquals("{\"SUCCESS\":\"Username has been added as one of the store managers successfully\"}", output1);
        service.removeStoreManager(session_id, "Aviv","Rami Levi");
    }

    @Test
    public void storeDoesNotExists(){
        try{
            String output1 = service.addStoreManager(session_id, "Aviv","Osher Ad");
            fail();
        }catch(Exception e) {
            assertEquals("This store doesn't exist", e.getMessage());
        }
    }

    @Test
    public void userDoesNotExists(){
        try{
            String output = service.addStoreManager(session_id, "Ben", "Rami Levi");
            fail();
        }catch(Exception e) {
            assertEquals("This username doesn't exist", e.getMessage());
        }
    }

//    @Test
//    public void doesNotAStoreOwner(){
//        try{
//            String output = service.addStoreManager(session_id, "Aviv", "Shufersal");
//            fail();
//        }catch(Exception e) {
//            assertEquals("You must be a store owner for this action", e.getMessage());
//        }
//    }

    @Test
    public void alreadyAManager(){
        String output1 = service.addStoreManager(session_id, "Aviv", "Rami Levi");
        assertEquals("{\"SUCCESS\":\"Username has been added as one of the store managers successfully\"}", output1);
        try{
            String output2 = service.addStoreManager(session_id, "Aviv", "Rami Levi");
            fail();
        }catch(Exception e) {
            assertEquals("This username is already one of the store's managers", e.getMessage());
        }
    }

    @Test
    public void emptyInput(){
        try{
            String output1 = service.addStoreManager(session_id, "","Rami Levi");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
        try{
            String output2 = service.addStoreManager(session_id, null,"Rami Levi");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
        try{
            String output3 = service.addStoreManager(session_id, "Aviv","");
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
        try{
            String output4 = service.addStoreManager(session_id, "Aviv",null);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter username and store name", e.getMessage());
        }
    }

}
