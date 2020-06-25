package IntegrationTests;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.Models.Store;
import DomainLayer.TradingSystem.Models.User;
import DomainLayer.TradingSystem.NotificationSystem;
import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StoreOwningIntegration {
    @BeforeClass
    public static void init() throws SQLException {
        PersistenceController.initiate(false);
        (new UsersHandler()).register("noy", "1234");
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).register("rachel", "1234");
        (new UsersHandler()).register("zuzu", "1234");
        (new UsersHandler()).register("lior", "1234");

        (new UsersHandler()).register("toya", "1234");
        UUID se = SystemFacade.getInstance().createNewSession();

        (new UsersHandler()).login(se,"toya", "1234", false);
        SystemFacade.getInstance().openNewStore(se, "Castro", "clothes for women and men");
        SystemFacade.getInstance().openNewStore(se,"Lalin", "beauty products");
        SystemFacade.getInstance().updateInventory("Castro", "white T-shirt", 5.0, "Clothing", "white T-shirt for men", 50);
        SystemFacade.getInstance().updateInventory("Lalin", "Body Scrub ocean", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);
        SystemFacade.getInstance().appointOwner(se, "rachel", "Castro");

        (new UsersHandler()).logout(se);


    }

    @AfterClass
    public static void clean() throws SQLException {
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Test
    public void validAppointmentTest() throws SQLException {
        UUID se =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se,"toya", "1234", false);
        Store store = SystemFacade.getInstance().getStoreByName("Lalin");
        int noyNumOfNotificBefore = NotificationSystem.getInstance().getUserNotificationNumber("noy");
        User noy = SystemFacade.getInstance().getUserByName("noy");
        assertTrue(!store.isOwner(noy));
        assertTrue(!noy.getStoreOwnings().containsKey(store));
        SystemFacade.getInstance().appointOwner(se,"noy", "Lalin");
        // noy get notification
        int noyNumOfNotificAfter = NotificationSystem.getInstance().getUserNotificationNumber("noy");
        assertTrue(store.isOwner(noy));
        assertTrue(noy.getStoreOwnings().containsKey(store));
        assertEquals(noyNumOfNotificBefore+1,noyNumOfNotificAfter);
        //SystemFacade.getInstance().editPermissions()

        SystemFacade.getInstance().logout(se);
    }

    @Test
    public void validAppointmentTest2() throws SQLException {
        UUID se =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se,"toya", "1234", false);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        User zuzu = SystemFacade.getInstance().getUserByName("zuzu");
        assertTrue(!store.isOwner(zuzu));
        assertTrue(!zuzu.getStoreOwnings().containsKey(store));
        int zuzuNumOfNotificBefore = NotificationSystem.getInstance().getUserNotificationNumber("zuzu");
        int rachelNumOfNotificBefore = NotificationSystem.getInstance().getUserNotificationNumber("rachel");

        SystemFacade.getInstance().appointOwner(se,"zuzu", "Castro");
        // zuzu get notification
        //rachel get notification

        int zuzuNumOfNotificAfter = NotificationSystem.getInstance().getUserNotificationNumber("zuzu");
        int rachelNumOfNotificAfter = NotificationSystem.getInstance().getUserNotificationNumber("rachel");
        assertEquals(zuzuNumOfNotificBefore+1,zuzuNumOfNotificAfter);
        assertEquals(rachelNumOfNotificBefore+1,rachelNumOfNotificAfter);

        SystemFacade.getInstance().logout(se);
        UUID se2 =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se2,"rachel", "1234", false);
        SystemFacade.getInstance().responseToAppointment(se2,"Castro", "zuzu", true);

        assertTrue(store.isOwner(zuzu));
        assertTrue(zuzu.getStoreOwnings().containsKey(store));

        SystemFacade.getInstance().logout(se2);
    }

    @Test
    public void notApprovedAddTest() throws SQLException {
        UUID se =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se,"toya", "1234", false);
        SystemFacade.getInstance().openNewStore(se, "P&B", "Clothing");
        Store store = SystemFacade.getInstance().getStoreByName("P&B");
        User maor = SystemFacade.getInstance().getUserByName("maor");
        User rachel = SystemFacade.getInstance().getUserByName("rachel");

        SystemFacade.getInstance().appointOwner(se,"rachel", "P&B");
        assertTrue(store.isOwner(rachel));
        assertTrue(rachel.getStoreOwnings().containsKey(store));
        int maorNumOfNotificBefore = NotificationSystem.getInstance().getUserNotificationNumber("maor");
        int rachelNumOfNotificBefore = NotificationSystem.getInstance().getUserNotificationNumber("rachel");

        assertTrue(!store.isOwner(maor));
        assertTrue(!maor.getStoreOwnings().containsKey(store));
        SystemFacade.getInstance().appointOwner(se,"maor", "P&B");
        // maor get notification
        //rachel get notification
        int maorNumOfNotificAfter = NotificationSystem.getInstance().getUserNotificationNumber("maor");
        int rachelNumOfNotificAfter = NotificationSystem.getInstance().getUserNotificationNumber("rachel");
        assertEquals(maorNumOfNotificBefore+1,maorNumOfNotificAfter);
//        assertEquals(rachelNumOfNotificBefore+1,rachelNumOfNotificAfter);

        SystemFacade.getInstance().logout(se);
        UUID se2 =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se2,"rachel", "1234", false);
        SystemFacade.getInstance().responseToAppointment(se2,"P&B", "maor", false);
        //maor get notification
        assertTrue(!store.isOwner(maor));
        assertTrue(!maor.getStoreOwnings().containsKey(store));

        SystemFacade.getInstance().logout(se2);
    }

    @Test
    public void validRemoveTest() throws SQLException {
        UUID se =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se, "toya", "1234", false);
        SystemFacade.getInstance().openNewStore(se,"Zara", "Clothing");
        Store store = SystemFacade.getInstance().getStoreByName("Zara");
        User lior = SystemFacade.getInstance().getUserByName("lior");
        User zuzu = SystemFacade.getInstance().getUserByName("zuzu");

        SystemFacade.getInstance().appointOwner(se,"lior", "Zara");
        assertTrue(store.isOwner(lior));
        assertTrue(lior.getStoreOwnings().containsKey(store));
        SystemFacade.getInstance().logout(se);

        UUID se2 =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se2, "lior", "1234", false);
        int zuzuNumOfNotificBefore = NotificationSystem.getInstance().getUserNotificationNumber("zuzu");

        SystemFacade.getInstance().appointManager(se2, "zuzu", "Zara");
        //zuzu get notification

        int zuzuNumOfNotificAfter = NotificationSystem.getInstance().getUserNotificationNumber("rachel");
        assertEquals(zuzuNumOfNotificBefore+1,zuzuNumOfNotificAfter);
        SystemFacade.getInstance().logout(se2);

        UUID se3 =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se3, "toya", "1234", false);
        SystemFacade.getInstance().removeStoreOwner("lior", "Zara");
        assertTrue(!store.isOwner(lior));
        assertTrue(!lior.getStoreOwnings().containsKey(store));
        assertTrue(!store.isManager(zuzu));
        assertTrue(!lior.getStoreManagements().containsKey(store));
        SystemFacade.getInstance().logout(se3);
    }

    @Test
    public void notOwnerRemoveTest() throws SQLException {
        UUID se =  SystemFacade.getInstance().createNewSession();

        (new UsersHandler()).login(se,"toya", "1234", false);
        SystemFacade.getInstance().appointOwner(se, "rachel", "Castro");

        SystemFacade.getInstance().openNewStore(se, "Bershka", "Clothing");
        Store store = SystemFacade.getInstance().getStoreByName("Bershka");
        Store castro = SystemFacade.getInstance().getStoreByName("Castro");

        User maor = SystemFacade.getInstance().getUserByName("maor");
        User rachel = SystemFacade.getInstance().getUserByName("rachel");
        assertTrue(castro.isOwner(rachel));
        SystemFacade.getInstance().appointOwner(se,"maor", "Bershka");
        assertTrue(store.isOwner(maor));
        assertTrue(maor.getStoreOwnings().containsKey(store));
        SystemFacade.getInstance().logout(se);

        UUID se2 =  SystemFacade.getInstance().createNewSession();

        (new UsersHandler()).login(se2,"maor", "1234", false);

        try{
            (new StoreHandler()).removeStoreOwner(se2,"rachel", "Castro");
        }
        catch (Exception e){
            assertEquals("You must be this store owner for this command", e.getMessage());

        }
        assertTrue(rachel.getStoreOwnings().containsKey(castro));

        assertTrue(castro.isOwner(rachel));

        SystemFacade.getInstance().logout(se2);
    }
}
