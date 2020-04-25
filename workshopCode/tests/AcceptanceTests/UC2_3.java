package AcceptanceTests;

import DomainLayer.Security.SecurityHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.HashMap;

import static org.junit.Assert.*;

public class UC2_3 {

    private UsersHandler service;

    @Before
    public void setUp() throws Exception {
        service = new UsersHandler();

    }

    @BeforeClass
    public static void init() throws Exception{
        (new UsersHandler()).register("lior", "1234");
        (new UsersHandler()).register("Amit", "good");
        (new UsersHandler()).addAdmin("lior");
    }


    @AfterClass
    public static void clean() {
        (new UsersHandler()).resetUsers();
        (new UsersHandler()).resetAdmins();
    }

    @After
    public void tearDown() throws Exception {
        (new UsersHandler()).logout();
    }

    @Test
    public void successfully() {
        String output1 = service.login("lior", "1234", true);
        assertEquals("You have been successfully logged in!", output1);
        service.logout();
        String output2 = service.login("Amit", "good", false);
        assertEquals("You have been successfully logged in!", output2);
    }

    @Test
    public void incorrectPassword() {
        String output = service.login("lior", "notgood", false);
        assertEquals("This password is incorrect", output);
    }

    @Test
    public void usernameNotExist() {
        String output = service.login("hassan", "good", false);
        assertEquals("This user is not registered", output);
    }
    @Test
    public void NotAdmin() {
        String output = service.login("Amit", "good", true);
        assertEquals("this user is not a system admin", output);
    }

    @Test
    public void emptyUsername() {
        String output1 = service.login(null, "1234", false);
        assertEquals("The username is invalid" , output1);
        String output2 = service.login("", "good", false);
        assertEquals("The username is invalid" , output2);
    }

}