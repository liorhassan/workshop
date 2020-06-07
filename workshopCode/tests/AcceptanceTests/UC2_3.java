package AcceptanceTests;

import ServiceLayer.SessionHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.*;

public class UC2_3 {

    private static UUID session_id;

    @BeforeClass
    public static void setUp() {
        session_id = (new SessionHandler()).openNewSession();
        (new UsersHandler()).register("toya", "1234");
    }

    @BeforeClass
    public static void init() throws Exception{
        session_id = (new SessionHandler()).openNewSession();
        (new UsersHandler()).register("lior", "1234");
        (new UsersHandler()).register("Amit", "good");
        (new UsersHandler()).addAdmin("lior");
    }


    @AfterClass
    public static void clean() {
        (new UsersHandler()).resetUsers();
        (new UsersHandler()).resetAdmins();
        (new SessionHandler()).closeSession(session_id);
    }

    @After
    public void tearDown() throws Exception {
        (new UsersHandler()).logout(session_id);
    }

    @Test
    public void successfully() {
        String output1 = (new UsersHandler()).login(session_id, "lior", "1234", true);
        assertEquals("{\"SUCCESS\":\"You have been successfully logged in!\"}", output1);
        (new UsersHandler()).logout(session_id);
        String output2 = (new UsersHandler()).login(session_id , "Amit", "good", false);
        assertEquals("{\"SUCCESS\":\"You have been successfully logged in!\"}", output2);
    }

    @Test
    public void incorrectPassword() {
        try{
            String output = (new UsersHandler()).login(session_id, "lior", "notgood", false);
            fail();
        }catch(Exception e) {
            assertEquals("This password is incorrect", e.getMessage());
        }
    }

    @Test
    public void usernameNotExist() {
        try{
            String output = (new UsersHandler()).login(session_id, "hassan", "good", false);
            fail();
        }catch(Exception e) {
            assertEquals("This user is not registered", e.getMessage());
        }
    }
    @Test
    public void NotAdmin() {
        try{
            String output = (new UsersHandler()).login(session_id, "Amit", "good", true);
            fail();
        }catch(Exception e) {
            assertEquals("this user is not a system admin", e.getMessage());
        }
    }

    @Test
    public void emptyUsername() {
        try{
            String output1 = (new UsersHandler()).login(session_id, null, "1234", false);
            fail();
        }catch(Exception e) {
            assertEquals("The username is invalid", e.getMessage());
        }
        try{
            String output2 = (new UsersHandler()).login(session_id, "", "good", false);
            fail();
        }catch(Exception e) {
            assertEquals("The username is invalid", e.getMessage());
        }
    }

}