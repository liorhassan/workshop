package IntegrationTests;

import DomainLayer.TradingSystem.Models.Store;
import DomainLayer.TradingSystem.Models.User;
import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.StoreHandler;
import ServiceLayer.StoreManagerHandler;
import ServiceLayer.UsersHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StoreOwningIntegration {
    @BeforeClass
    public static void init(){

        (new UsersHandler()).register("noy", "1234");
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).register("rachel", "1234");
        (new UsersHandler()).register("zuzu", "1234");
        (new UsersHandler()).register("lior", "1234");
        (
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
    public static void clean(){
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Test
    public void validAppointmentTest(){
        UUID se =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se,"toya", "1234", false);
        Store store = SystemFacade.getInstance().getStoreByName("Lalin");
        User noy = SystemFacade.getInstance().getUserByName("noy");
        assertTrue(!store.isOwner(noy));
        assertTrue(!noy.getStoreOwnings().contains(store));
        SystemFacade.getInstance().appointOwner(se,"noy", "Lalin");
        // noy get notification
        assertTrue(store.isOwner(noy));
        assertTrue(noy.getStoreOwnings().contains(store));

        //SystemFacade.getInstance().editPermissions()

        SystemFacade.getInstance().logout(se);
    }

    @Test
    public void validAppointmentTest2(){
        UUID se =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se,"toya", "1234", false);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        User zuzu = SystemFacade.getInstance().getUserByName("zuzu");
        assertTrue(!store.isOwner(zuzu));
        assertTrue(!zuzu.getStoreOwnings().contains(store));
        SystemFacade.getInstance().appointOwner(se,"zuzu", "Castro");
        // maor get notification
        //rachel get notification
        SystemFacade.getInstance().logout(se);
        UUID se2 =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se,"rachel", "1234", false);
        SystemFacade.getInstance().approve("maor", "Castro");
        //maor get notification
        assertTrue(store.isOwner(zuzu));
        assertTrue(zuzu.getStoreOwnings().contains(store));

        SystemFacade.getInstance().logout(se2);
    }

    @Test
    public void notApprovedAddTest(){
        UUID se =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se,"toya", "1234", false);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        User maor = SystemFacade.getInstance().getUserByName("maor");
        assertTrue(!store.isOwner(maor));
        assertTrue(!maor.getStoreOwnings().contains(store));
        SystemFacade.getInstance().appointOwner(se,"maor", "Castro");
        // maor get notification
        //rachel get notification
        SystemFacade.getInstance().logout(se);
        UUID se2 =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se,"rachel", "1234", false);
        SystemFacade.getInstance().declined("maor", "Castro");
        //maor get notification
        assertTrue(!store.isOwner(maor));
        assertTrue(!maor.getStoreOwnings().contains(store));

        SystemFacade.getInstance().logout(se2);
    }

    @Test
    public void validRemoveTest(){
        UUID se =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se, "toya", "1234", false);
        SystemFacade.getInstance().openNewStore(se,"Zara", "Clothing");
        Store store = SystemFacade.getInstance().getStoreByName("Zara");
        User lior = SystemFacade.getInstance().getUserByName("lior");
        User zuzu = SystemFacade.getInstance().getUserByName("zuzu");

        SystemFacade.getInstance().appointManager(se,"lior", "Zara");
        assertTrue(store.isOwner(lior));
        assertTrue(lior.getStoreOwnings().contains(store));
        SystemFacade.getInstance().logout(se);

        UUID se2 =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se2, "lior", "1234", false);
        SystemFacade.getInstance().appointManager(se2, "zuzu", "Zara");
        //zuzu get notification
        SystemFacade.getInstance().logout(se2);

        UUID se3 =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se3, "toya", "1234", false);
        SystemFacade.getInstance().removeOwner(se3, "lior", "Zara");
        assertTrue(!store.isOwner(lior));
        assertTrue(!lior.getStoreOwnings().contains(store));
        assertTrue(!store.isManager(zuzu));
        assertTrue(!lior.getStoreManagements().contains(store));
        SystemFacade.getInstance().logout(se3);
    }

    @Test
    public void notOwnerRemoveTest(){
        UUID se =  SystemFacade.getInstance().createNewSession();

        (new UsersHandler()).login(se,"toya", "1234", false);
        SystemFacade.getInstance().openNewStore(se, "Bershka", "Clothing");
        Store store = SystemFacade.getInstance().getStoreByName("Bershka");
        User maor = SystemFacade.getInstance().getUserByName("maor");
        SystemFacade.getInstance().appointOwner(se,"maor", "Bershka");
        assertTrue(store.isOwner(maor));
        assertTrue(maor.getStoreOwnings().contains(store));
        SystemFacade.getInstance().logout(se);

        UUID se2 =  SystemFacade.getInstance().createNewSession();

        (new UsersHandler()).login(se,"maor", "1234", false);

        try{
            SystemFacade.getInstance().removeOwner("rachel", "Castro");
        }
        catch (Exception e){
            assertEquals("You must be a store owner for this action", e.getMessage());

        }
        assertTrue(store.isManager(rachel));
        assertTrue(rachel.getStoreManagements().contains(SystemFacade.getInstance().getStoreByName("Castro")));

        SystemFacade.getInstance().logout(se2);
    }
}
