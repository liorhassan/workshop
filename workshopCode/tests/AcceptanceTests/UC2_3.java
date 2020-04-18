package AcceptanceTests;

import DomainLayer.Security.SecurityHandler;
import DomainLayer.SystemHandler;
import DomainLayer.User;
import ServiceLayer.Login;
import org.junit.*;

import java.util.HashMap;

import static org.junit.Assert.*;

public class UC2_3 {

    private Login login;

    @Before
    public void setUp() throws Exception {
        login = new Login();

    }

    @BeforeClass
    public static void init() throws Exception{
        SystemHandler.getInstance().register("lior");
    }


    @AfterClass
    public static void clean() {
        SystemHandler.getInstance().setUsers(new HashMap<>());
    }

    @After
    public void tearDown() throws Exception {
        SystemHandler.getInstance().logout();
    }

    @Test
    public void successfully() {
        String output = login.login("lior", "good");
        assertEquals("You have been successfully logged in!", output);
    }

    @Test
    public void inCorrectPassword() {
        String output = login.login("lior", "notgood");
        assertEquals("This password is incorrect", output);
    }

    @Test
    public void usernameNotExist() {
        String output = login.login("hassan", "good");
        assertEquals("This user is not registered", output);
    }

    @Test
    public void emptyUsername() {
        String output1 = login.login(null, "1234");
        assertEquals("The username is invalid" , output1);
        String output2 = login.login("", "gooood");
        assertEquals("The username is invalid" , output2);
    }

}