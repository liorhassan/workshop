package UnitTests;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.Models.Store;
import DomainLayer.TradingSystem.Models.User;
import DomainLayer.TradingSystem.StoreManaging;
import DomainLayer.TradingSystem.StoreOwning;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

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
        user.setUsername("tester");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void hasEditPrivileges() throws SQLException {
        User user2 = new User();
        user2.setUsername("tester2");
        StoreOwning storeOwning = new StoreOwning("Fox", "tester");
        StoreOwning storeOwning2 = new StoreOwning("Castro", "tester2");
        StoreManaging storeManaging = new StoreManaging(user2,"Castro", user.getUsername());
        Store store1 = new Store("Fox",null,user,storeOwning);
        Store store2 = new Store("Castro",null,user,storeOwning2);
        assertFalse(user.hasEditPrivileges("Fox"));
        assertFalse(user.hasEditPrivileges("Castro"));
        user.addOwnedStore(store1,storeOwning);
        user.addManagedStore(store2, storeManaging);
        assertTrue(user.hasEditPrivileges("Fox"));
        assertTrue(user.hasEditPrivileges("Castro"));
    }
}