package UnitTests;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.Models.Store;
import DomainLayer.TradingSystem.Models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    User user;

    @BeforeClass
    public static void init() {
        PersistenceController.initiate(false);
    }

    @Before
    public void setUp() throws Exception {
        user = new User();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void hasEditPrivileges() {
        Store store1 = new Store("Fox",null,null,null);
        Store store2 = new Store("Castro",null,null,null);
        assertFalse(user.hasEditPrivileges("Fox"));
        assertFalse(user.hasEditPrivileges("Castro"));
        user.addOwnedStore(store1,null);
        user.addManagedStore(store2,null);
        assertTrue(user.hasEditPrivileges("Fox"));
        assertTrue(user.hasEditPrivileges("Castro"));
    }
}