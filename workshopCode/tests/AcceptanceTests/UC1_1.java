package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.SystemInitHandler;
import ServiceLayer.UsersHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC1_1 {

    private static SystemInitHandler handler;
    private static UUID session_id;

    @BeforeClass
    public static void setUp() throws Exception {
        PersistenceController.initiate(false);
        session_id = (new SessionHandler()).openNewSession();
        handler = new SystemInitHandler();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Test
    public void validFileTest() throws Exception {
        handler.initSystem("../configurations/init.ini");
        String result = (new UsersHandler()).login(session_id, "u1", "1234", true);
        assertEquals("{\"SUCCESS\":\"You have been successfully logged in!\"}", result);
        (new UsersHandler()).logout(session_id);
        try{
            result = (new UsersHandler()).login(session_id, "Admin159", "951", true);
        }
        catch (Exception e){
            assertEquals("This user is not registered", e.getMessage());
        }
    }

    @Test
    public void inValidFileTest() throws Exception {
        try{
            handler.initSystem("test2.ini");
        }
        catch (Exception e){
           // assertEquals("{\"SUCCESS\":\"You have been successfully logged in!\"}", e.getMessage());
        }
        String result = (new UsersHandler()).login(session_id, "Admin159", "951", true);
        assertEquals("{\"SUCCESS\":\"You have been successfully logged in!\"}", result);
        (new UsersHandler()).logout(session_id);
    }

}
