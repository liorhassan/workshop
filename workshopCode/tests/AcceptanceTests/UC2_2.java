package AcceptanceTests;

import ServiceLayer.UsersHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class UC2_2 {

    private UsersHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new UsersHandler();
    }

    @After
    public void tearDown() throws Exception {
        (new UsersHandler()).resetUsers();
    }

    @Test
    public void valid() {
        String result = handler.register("shenhav", "toya");
        assertEquals("You have been successfully registered!", result);
    }

    @Test
    public void invalidPassword() {
        String result = handler.register("shenhav", "++");
        assertEquals("This password is not valid. Please choose a different one", result);
    }

    @Test
    public void usernameExist() {
        handler.register("shenhav", "toya");
        String result = handler.register("shenhav", "toya2");
        assertEquals("This username already exists in the system. Please choose a different one", result);
    }

    @Test
    public void emptyUsername() {
        String result1 = handler.register("", "toya");
        assertEquals("Username cannot be empty", result1);
        String result2 = handler.register(null, "toya");
        assertEquals("Username cannot be empty", result2);
    }

}