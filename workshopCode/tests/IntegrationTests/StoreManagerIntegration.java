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

public class StoreManagerIntegration {
    @BeforeClass
    public static void init(){

        (new UsersHandler()).register("noy", "1234");
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).register("rachel", "1234");
        (new UsersHandler()).register("zuzu", "1234");
        (new UsersHandler()).register("toya", "1234");
        UUID se = SystemFacade.getInstance().createNewSession();

        (new UsersHandler()).login(se,"toya", "1234", false);
        SystemFacade.getInstance().openNewStore(se, "Castro", "clothes for women and men");
        SystemFacade.getInstance().openNewStore(se,"Lalin", "beauty products");
        SystemFacade.getInstance().updateInventory("Castro", "white T-shirt", 5.0, "Clothing", "white T-shirt for men", 50);
        SystemFacade.getInstance().updateInventory("Lalin", "Body Scrub ocean", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);
        SystemFacade.getInstance().appointManager(se, "rachel", "Castro");

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
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        User noy = SystemFacade.getInstance().getUserByName("noy");
        assertTrue(!store.isManager(noy));
        assertTrue(!noy.getStoreManagements().contains(store));
        SystemFacade.getInstance().appointManager(se,"noy", "Castro");

        assertTrue(store.isManager(noy));
        assertTrue(noy.getStoreManagements().contains(store));

        SystemFacade.getInstance().logout(se);
    }

    @Test
    public void notOwnerAddTest(){
        UUID se =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se,"maor", "1234", false);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        User zuzu = SystemFacade.getInstance().getUserByName("zuzu");
        assertTrue(!store.isManager(zuzu));
        assertTrue(!zuzu.getStoreManagements().contains(store));
        try{
            (new StoreManagerHandler()).addStoreManager(se,"zuzu", "Castro");
        }
        catch (Exception e){
            assertEquals("You must be a store owner for this action", e.getMessage());

        }
        assertTrue(!store.isManager(zuzu));
        assertTrue(!zuzu.getStoreManagements().contains(store));

        SystemFacade.getInstance().logout(se);
    }

    @Test
    public void validRemoveTest(){
        UUID se =  SystemFacade.getInstance().createNewSession();
        (new UsersHandler()).login(se, "toya", "1234", false);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        User maor = SystemFacade.getInstance().getUserByName("maor");
        SystemFacade.getInstance().appointManager(se,"maor", "Castro");
        assertTrue(store.isManager(maor));
        assertTrue(maor.getStoreManagements().contains(store));
        SystemFacade.getInstance().removeManager("maor", "Castro");
        assertTrue(!store.isManager(maor));
        assertTrue(!maor.getStoreManagements().contains(store));
        SystemFacade.getInstance().logout(se);
    }

    @Test
    public void notOwnerRemoveTest(){
        UUID se =  SystemFacade.getInstance().createNewSession();

        (new UsersHandler()).login(se,"noy", "1234", false);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        User rachel = SystemFacade.getInstance().getUserByName("rachel");

        assertTrue(store.isManager(rachel));
        assertTrue(rachel.getStoreManagements().contains(store));
        try{
            SystemFacade.getInstance().removeManager("rachel", "Castro");
        }
        catch (Exception e){
            assertEquals("You must be a store owner for this action", e.getMessage());

        }
        assertTrue(store.isManager(rachel));
        assertTrue(rachel.getStoreManagements().contains(store));

        SystemFacade.getInstance().logout(se);
    }

}
