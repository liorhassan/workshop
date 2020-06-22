package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UC2_2 {

    private static UsersHandler handler;

    @BeforeClass
    public static void setUp() throws Exception {
        PersistenceController.initiate(false);
        handler = new UsersHandler();
    }

    @After
    public void tearDown() throws Exception {
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Test
    public void valid() {
        String result = handler.register("shenhav", "toya");
        assertEquals("{\"SUCCESS\":\"You have been successfully registered!\"}", result);
    }

    @Test
    public void invalidPassword() {
        try {
            String result = handler.register("shenhav", "++");
            fail();
        }catch (Exception e){
            assertEquals("This password is not valid. Please choose a different one",e.getMessage());
        }
    }

    @Test
    public void usernameExist() {
        handler.register("shenhav", "toya");
        try {
            String result = handler.register("shenhav", "toya2");
            fail();
        }catch(Exception e){
            assertEquals("This username already exists in the system. Please choose a different one", e.getMessage());
        }

    }

    @Test
    public void emptyUsername() {
        try{
            String result1 = handler.register("", "toya");
            fail();
        }catch(Exception e) {
            assertEquals("Username cannot be empty", e.getMessage());
        }
        try{
            String result2 = handler.register(null, "toya");
            fail();
        }catch(Exception e) {
            assertEquals("Username cannot be empty", e.getMessage());
        }
    }

}