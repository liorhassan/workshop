package UnitTests;

import DomainLayer.*;

import DomainLayer.Models.Basket;
import DomainLayer.Models.Store;
import DomainLayer.Models.User;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class SystemHandler_UT {

    private SystemHandler sys;

    @Before
    public void setUp(){
        sys = SystemHandler.getInstance();
    }

    @BeforeClass
    public static void init(){
        SystemHandler.getInstance().register("prince");
        SystemHandler.getInstance().addAdmin("prince");
        SystemHandler.getInstance().register("loco");

        SystemHandler.getInstance().getUsers().put("noy", new User());
        SystemHandler.getInstance().getUsers().get("noy").setUsername("noy");
        SystemHandler.getInstance().getUsers().put("zuzu", new User());
        SystemHandler.getInstance().getUsers().get("zuzu").setUsername("zuzu");
        Store s = new Store("Pull&Bear", "clothing", SystemHandler.getInstance().getUsers().get("noy"),new StoreOwning());
        s.addManager(SystemHandler.getInstance().getUsers().get("zuzu"), new StoreManaging(SystemHandler.getInstance().getUsers().get("zuzu")));
        s.addManager(SystemHandler.getInstance().getUsers().get("loco"), new StoreManaging(SystemHandler.getInstance().getUsers().get("loco")));
        s.updateInventory("skinny jeans", 120, Category.Clothing, "The most comfortable skiny jeans", 3);
        s.updateInventory("blue top", 120, Category.Clothing, "pretty blue crop top", 4);
    }

    @AfterClass
    public static void clean(){

    }

    @Test
    public void removeManager_Test(){
        sys.login("noy", false);
        assertEquals("Manager removed successfully!", sys.removeManager("loco", "Pull&Bear"));
        assertEquals("Manager wasn't removed", sys.removeManager("maor", "Pull&Bear"));
        assertEquals("Manager wasn't removed", sys.removeManager("loco", "Swear"));
    }

    @Test
    public void isUserAppointer_Test(){
        sys.login("noy", false);
        assertTrue(sys.isUserAppointer("zuzu", "Pull&Bear"));
        assertFalse(sys.isUserAppointer("prince", "Pull&Bear"));
        assertFalse(sys.isUserAppointer("maor", "Swear"));
        sys.logout();
    }

    @Test
    public void checkIfProductExists_Test(){
        assertTrue(sys.checkIfProductExists("Pull&Bear", "skinny jeans"));
        assertFalse(sys.checkIfProductExists("Pull&Bear", "white top"));
        assertFalse(sys.checkIfProductExists("Swear", "black leggings"));
    }

    @Test
    public void editPermissions_Test(){
        sys.login("noy", false);
        List<String> p = new LinkedList<>();
        p.add("Define Purchase Policy And Type");
        sys.editPermissions("zuzu", p, "Pull&Bear");
        assertTrue(sys.getStoreByName("Pull&Bear").getManagements().get(sys.getUserByName("zuzu")).getPermission().contains(new Permission("Define Purchase Policy And Type")));
        assertFalse(sys.getStoreByName("Pull&Bear").getManagements().get(sys.getUserByName("zuzu")).getPermission().contains(new Permission("View Store Purchase History")));
        sys.logout();
    }

    @Test
    public void purchaseBaskets_Test(){
        sys.login("noy", false);
        Store s = sys.getStoreByName("Pull&Bear");
        Basket b = new Basket(s);
        b.addProduct(s.getProductByName("skinny jeans"), 2);
        b.addProduct(s.getProductByName("blue top"), 4);
        sys.getActiveUser().getShoppingCart().addBasket(b);
        sys.purchaseBaskets();
        assertFalse(s.checkIfProductAvailable("blue top", 1));
        assertFalse(s.checkIfProductAvailable("blue top", 2));
        assertTrue(s.checkIfProductAvailable("skinny jeans", 1));
    }


    @Test
    public void appointOwner_Test(){
        sys.login("noy", false);
        sys.appointOwner("prince", "Pull&Bear");
        assertTrue(sys.getStoreByName("Pull&Bear").getOwnerships().containsKey(sys.getUserByName("prince")));
        sys.logout();
    }

    @Test
    public void checkIfActiveUserIsOwner_Test(){
        sys.login("noy", false);
        assertTrue(sys.checkIfActiveUserIsOwner("Pull&Bear"));
        sys.logout();
        sys.login("zuzu", false);
        assertFalse(sys.checkIfActiveUserIsOwner("Pull&Bear"));
        sys.logout();
    }

    @Test
    public void checkIfUserIsOwner_Test(){
        sys.login("noy", false);
        assertFalse(sys.checkIfUserIsOwner("Pull&Bear", "zuzu"));
        assertTrue(sys.checkIfUserIsOwner("Pull&Bear", "noy"));
        sys.logout();
    }

    @Test
    public void checkIfActiveUserIsManager_Test(){
        sys.login("noy", false);
        assertFalse(sys.checkIfActiveUserIsManager("Pull&Bear"));
        sys.logout();
        sys.login("zuzu", false);
        assertTrue(sys.checkIfActiveUserIsManager("Pull&Bear"));
        sys.logout();
    }


    @Test
    public void checkIfUserIsManager_Test(){
        sys.login("noy", false);
        assertTrue(sys.checkIfUserIsManager("Pull&Bear", "zuzu"));
        assertFalse(sys.checkIfUserIsManager("Pull&Bear", "prince"));
        sys.logout();
    }

    @Test
    public void checkIfActiveUserSubscribed_Test(){
        assertFalse(sys.checkIfActiveUserSubscribed());
        sys.login("noy", false);
        assertTrue(sys.checkIfActiveUserSubscribed());
        sys.logout();
    }

    @Test
    public void checkIfUserHavePermission_Test(){
        sys.login("noy", false);
        List<Permission> p = new LinkedList<>();
        p.add(new Permission("View Store Purchase History"));
        sys.getStoreByName("Pull&Bear").getManagements().get(sys.getUserByName("zuzu")).setPermission(p);
        sys.logout();
        sys.login("zuzu", false);
        assertTrue(sys.checkIfUserHavePermission("Pull&Bear", "View Store Purchase History"));
        assertFalse(sys.checkIfUserHavePermission("Pull&Bear", "Appoint New Owner"));
        sys.logout();
    }
}
