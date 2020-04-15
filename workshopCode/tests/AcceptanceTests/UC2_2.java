package AcceptanceTests;

import DomainLayer.Security.SecurityHandler;
import DomainLayer.SystemHandler;
import ServiceLayer.Register;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class UC2_2 {

    private Register r;

    @Before
    public void setUp() throws Exception {
        r = new Register();
    }

    @After
    public void tearDown() throws Exception {
        SystemHandler.getInstance().setUsers(new HashMap<>());
    }

    @Test
    public void valid() {
        String result = r.register("shenhav", "toya");
        assertEquals("You have been successfully registered!", result);
    }

    @Test
    public void invalidPassword() {
        String result = r.register("shenhav", "++");
        assertEquals("This password is not valid. Please choose a different one", result);
    }

    @Test
    public void usernameExist() {
        r.register("shenhav", "toya");
        String result = r.register("shenhav", "toya2");
        assertEquals("This username already exists in the system. Please choose a different one", result);
    }

    @Test
    public void emptyUsername() {
        String result1 = r.register("", "toya");
        assertEquals("Username cannot be empty", result1);
        String result2 = r.register(null, "toya");
        assertEquals("Username cannot be empty", result2);
    }

}