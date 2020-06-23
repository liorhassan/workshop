package IntegrationTests;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.Models.Store;
import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.SystemInitHandler;
import ServiceLayer.UsersHandler;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SystemInitIntegration {

    private static SystemInitHandler handler;
    private static UUID session_id;

    @BeforeClass
    public static void setUp() throws Exception {
        PersistenceController.initiate(false);
        session_id = (new SessionHandler()).openNewSession();
        handler = new SystemInitHandler();
        handler.initSystem("init.ini");

    }

    @After
    public void tearDown() throws Exception {
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Test
    public void usersInInitTests() throws Exception {
        assertTrue(SystemFacade.getInstance().userExists("user1"));
        assertTrue(SystemFacade.getInstance().userExists("user2"));
        assertTrue(SystemFacade.getInstance().userExists("user3"));
        assertTrue(SystemFacade.getInstance().checkIfUserIsAdmin("user1"));
        assertTrue(!SystemFacade.getInstance().userExists("Admin159"));

    }

    @Test
    public void storesInInitTests() throws Exception {
        assertTrue(SystemFacade.getInstance().storeExists("store1"));
        assertTrue(!SystemFacade.getInstance().storeExists("store2"));
        assertTrue(SystemFacade.getInstance().checkIfProductExists("store1", "diapers"));
        assertTrue(SystemFacade.getInstance().checkIfUserIsOwner("store1", "user2"));
        assertTrue(SystemFacade.getInstance().checkIfUserIsManager("store1", "user3"));
    }

    @Test
    public void managerAndOwnerInitTests() throws Exception {
        Store store = SystemFacade.getInstance().getStoreByName("store1");
        assertTrue(SystemFacade.getInstance().getUserByName("user3").getStoreManagements().containsKey(store));
        assertTrue(SystemFacade.getInstance().getUserByName("user2").getStoreOwnings().containsKey(store));
        assertTrue(!SystemFacade.getInstance().getUserByName("user1").getStoreManagements().containsKey(store));
        assertTrue(!SystemFacade.getInstance().getUserByName("user1").getStoreOwnings().containsKey(store));
        assertEquals(SystemFacade.getInstance().getUserByName("user3").getStoreManagements().get(store).getAppointer(), SystemFacade.getInstance().getUsers().get("user2"));
        assertTrue(store.isOwner(SystemFacade.getInstance().getUserByName("user2")));
        assertTrue(store.isManager(SystemFacade.getInstance().getUserByName("user3")));

        assertTrue(store.getManagements().get(SystemFacade.getInstance().getUserByName("user3")).havePermission("Add Manager"));
        assertTrue(store.getManagements().get(SystemFacade.getInstance().getUserByName("user3")).havePermission("Manage Supply"));
    }


}
