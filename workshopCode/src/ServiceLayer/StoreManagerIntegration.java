package ServiceLayer;

import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.Store;
import DomainLayer.TradingSystem.Models.User;
import DomainLayer.TradingSystem.SystemFacade;

import java.util.concurrent.ConcurrentHashMap;

public class StoreManagerIntegration {
    @BeforeClass
    public static void init(){
        (new UsersHandler()).register("noy", "1234");
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).register("rachel", "1234");
        (new UsersHandler()).register("zuzu", "1234");
        UC3_2.init(); // user toya is logged in
        (new StoreHandler()).openNewStore("Castro", "clothes for women and men");
        (new StoreHandler()).openNewStore("Lalin", "beauty products");
        (new StoreHandler()).UpdateInventory("Castro", "white T-shirt", 5.0, "Clothing", "white T-shirt for men", 50);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub ocean", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);
        (new StoreManagerHandler()).addStoreManager("rachel", "Castro");

        (new UsersHandler()).logout();


    }

    @Test
    public void validAppointmentTest(){
        (new UsersHandler()).login("toya", "1234", false);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        User noy = SystemFacade.getInstance().getUserByName("noy");
        assertTrue(!store.isManager(noy));
        assertTrue(!noy.getStoreManagements().contains(store));
        (new StoreManagerHandler()).addStoreManager("noy", "Castro");

        assertTrue(store.isManager(noy));
        assertTrue(noy.getStoreManagements().contains(store));

        (new UsersHandler()).logout();
    }

    @Test
    public void notOwnerAddTest(){
        (new UsersHandler()).login("maor", "1234", false);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        User zuzu = SystemFacade.getInstance().getUserByName("zuzu");
        assertTrue(!store.isManager(zuzu));
        assertTrue(!zuzu.getStoreManagements().contains(store));
        try{
            (new StoreManagerHandler()).addStoreManager("zuzu", "Castro");
        }
        catch (Exception e){
            assertEquals("You must be a store owner for this action", e.getMessage());

        }
        assertTrue(!store.isManager(zuzu));
        assertTrue(!zuzu.getStoreManagements().contains(store));

        (new UsersHandler()).logout();
    }

    @Test
    public void validRemoveTest(){
        (new UsersHandler()).login("toya", "1234", false);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        User maor = SystemFacade.getInstance().getUserByName("maor");
        (new StoreManagerHandler()).addStoreManager("maor", "Castro");
        assertTrue(store.isManager(maor));
        assertTrue(maor.getStoreManagements().contains(store));
        (new StoreManagerHandler()).removeStoreManager("maor", "Castro");
        assertTrue(!store.isManager(maor));
        assertTrue(!maor.getStoreManagements().contains(store));
        (new UsersHandler()).logout();
    }

    @Test
    public void notOwnerRemoveTest(){
        (new UsersHandler()).login("noy", "1234", false);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        User rachel = SystemFacade.getInstance().getUserByName("rachel");

        assertTrue(store.isManager(rachel));
        assertTrue(rachel.getStoreManagements().contains(store));
        try{
            (new StoreManagerHandler()).removeStoreManager("rachel", "Castro");
        }
        catch (Exception e){
            assertEquals("You must be a store owner for this action", e.getMessage());

        }
        assertTrue(store.isManager(rachel));
        assertTrue(rachel.getStoreManagements().contains(store));

        (new UsersHandler()).logout();
    }

}
