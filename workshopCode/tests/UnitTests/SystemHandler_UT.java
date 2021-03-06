package UnitTests;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Store;
import DomainLayer.TradingSystem.Models.User;
import DomainLayer.TradingSystem.*;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class SystemHandler_UT {

    private SystemFacade sys;

    @Before
    public void setUp(){
        sys = SystemFacade.getInstance();
    }

    @BeforeClass
    public static void init(){
        SystemFacade.getInstance().register("prince");
        SystemFacade.getInstance().addAdmin("prince");
        SystemFacade.getInstance().register("loco");
        SystemFacade.getInstance().getUsers().put("KING", new User());
        SystemFacade.getInstance().getUsers().get("KING").setUsername("KING");
        SystemFacade.getInstance().getUsers().put("noy", new User());
        SystemFacade.getInstance().getUsers().get("noy").setUsername("noy");
        SystemFacade.getInstance().getUsers().put("zuzu", new User());
        SystemFacade.getInstance().getUsers().get("zuzu").setUsername("zuzu");
        Store s = new Store("Pull&Bear", "clothing", SystemFacade.getInstance().getUsers().get("noy"),new StoreOwning("noy", "Pull&Bear"));
        SystemFacade.getInstance().getStores().put("Pull&Bear", s);
        s.addManager(SystemFacade.getInstance().getUsers().get("zuzu"), new StoreManaging(SystemFacade.getInstance().getUsers().get("noy"), "Pull&Bear","zuzu"), false);
        s.addManager(SystemFacade.getInstance().getUsers().get("loco"), new StoreManaging(SystemFacade.getInstance().getUsers().get("noy"), "Pull&Bear", "loco"), false);
        s.addToInventory("skinny jeans", 120, Category.Clothing, "The most comfortable skiny jeans", 3);
        s.addToInventory("blue top", 120, Category.Clothing, "pretty blue crop top", 4);
        Store s2 = new Store("Bershka", "clothing", SystemFacade.getInstance().getUsers().get("noy"),new StoreOwning("Bershka", "loco"));
        SystemFacade.getInstance().getStores().put("Bershka", s2);
        s2.addToInventory("skinny jeans", 100, Category.Clothing, "skinny jeans", 3);

    }

    @AfterClass
    public static void clean(){
        SystemFacade.getInstance().resetAdmins();
        SystemFacade.getInstance().resetStores();
        SystemFacade.getInstance().resetUsers();
    }

    @Test
    public void register_Test() {
        sys.register("lior");
        assertTrue(sys.getUsers().containsKey("lior"));
    }

    @Test
    public void login_Test1() {
        sys.logout();
        assertEquals(null, sys.getActiveUser().getUsername());
        sys.login("prince", true);
        assertEquals(sys.getUsers().get("prince"), sys.getActiveUser());
        assertTrue(sys.getActiveUser().getUsername().equals("prince"));
        assertTrue(sys.isAdminMode());
        sys.logout();
    }

    @Test
    public void login_Test2() {
        sys.login("loco", false);
        assertEquals(sys.getUsers().get("loco"), sys.getActiveUser());
        assertTrue(sys.getActiveUser().getUsername().equals("loco"));
        assertFalse(sys.isAdminMode());
        sys.logout();
    }

    @Test
    public void openNewStore_Test() {
        sys.login("loco", false);
        sys.openNewStore("ZARA", "clothing");
        assertTrue(sys.getStores().containsKey("ZARA"));
        assertTrue(sys.getActiveUser().getStoreOwnings().containsKey(sys.getStores().get("ZARA")));
        assertEquals(sys.getStores().get("ZARA").getStoreFirstOwner(),sys.getActiveUser());
        sys.logout();
    }


    @Test
    public void addToShoppingBasket_Test() {
        sys.addToShoppingBasket("Pull&Bear", "skinny jeans", 1);
        assertTrue(sys.checkIfBasketExists("Pull&Bear"));
        //String output =  sys.getActiveUser().getShoppingCart().getStoreBasket(sys.getStoreByName("Pull&Bear")).viewBasket();
        //assertEquals("Store name: Pull&Bear\nProduct name: skinny jeans price: 120.0 amount: 1\n",output);
    }



    @Test
    public void isProductAvailable_Test() {
        assertTrue(sys.isProductAvailable("Pull&Bear", "skinny jeans", 1));
        assertTrue(sys.isProductAvailable("Pull&Bear", "skinny jeans", 3));
        assertTrue(sys.isProductAvailable("Pull&Bear", "blue top", 4));
        assertFalse(sys.isProductAvailable("Pull&Bear", "blue top", 7));
        assertFalse(sys.isProductAvailable("Pull&Bear", "skinny jeans", 4));
    }

    @Test
    public void viewShoppingCart_Test() {
        sys.login("KING", false);
        String output = sys.viewSoppingCart();
        assertEquals("Your ShoppingCart details: \nempty!", output);
        sys.addToShoppingBasket("Pull&Bear", "skinny jeans", 1);
        String output1 = sys.viewSoppingCart();
        assertTrue(sys.checkIfBasketExists("Pull&Bear"));
        assertEquals("Your ShoppingCart details: \nStore name: Pull&Bear\nProduct name: skinny jeans price: 120.0 amount: 1\n", output1);
        sys.editShoppingCart("Pull&Bear", "skinny jeans", 0);
        sys.logout();
    }

    @Test
    public void editShoppingCart_Test() {
        sys.login("loco", false);
        sys.addToShoppingBasket("Pull&Bear", "skinny jeans", 1);
        String output = sys.viewSoppingCart();
        assertTrue(sys.checkIfBasketExists("Pull&Bear"));
        assertEquals("Your ShoppingCart details: \nStore name: Pull&Bear\nProduct name: skinny jeans price: 120.0 amount: 1\n", output);
        sys.editShoppingCart("Pull&Bear", "skinny jeans", 3);
        String output1 = sys.viewSoppingCart();
        assertEquals("Your ShoppingCart details: \nStore name: Pull&Bear\nProduct name: skinny jeans price: 120.0 amount: 3\n", output1);
        sys.logout();
    }

    @Test
    public void updateInventory_Test(){
        sys.login("noy", false);
        String output1 = sys.updateInventory("Pull&Bear", "t-shirt", 70, "Clothing","white t-shirt", 2);
        assertEquals("The product has been added", output1);
        String output2 = sys.updateInventory("Pull&Bear", "t-shirt", 60, "Clothing","white t-shirt", 4);
        assertEquals("The product has been updated", output2);
        sys.logout();
    }

    @Test
    public void appointManager_Test(){
        sys.login("noy", false);
        sys.appointManager("prince", "Pull&Bear");
        assertTrue(sys.getStoreByName("Pull&Bear").getManagements().containsKey(sys.getUserByName("prince")));
        sys.logout();
    }

    @Test
    public void removeManager_Test(){
        sys.login("noy", false);
        assertEquals("Manager removed successfully!", sys.removeManager("loco", "Pull&Bear"));
        assertEquals("Manager wasn't removed", sys.removeManager("maor", "Pull&Bear"));
        assertEquals("Manager wasn't removed", sys.removeManager("loco", "Swear"));
        sys.logout();
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
        assertTrue(sys.getStoreByName("Pull&Bear").getManagements().get(sys.getUserByName("zuzu")).havePermission("Define Purchase Policy And Type"));
        assertFalse(sys.getStoreByName("Pull&Bear").getManagements().get(sys.getUserByName("zuzu")).getPermission().contains(new Permission("View Store Purchase History")));
        sys.logout();
    }

    @Test
    public void preserveProducts_Test(){
        sys.login("noy", false);
        Store s = sys.getStoreByName("Pull&Bear");
        Basket b = new Basket(s, sys.getActiveUser().getShoppingCart());
        b.addProduct(s.getProductByName("skinny jeans"), 2);
        b.addProduct(s.getProductByName("blue top"), 4);
        sys.getActiveUser().getShoppingCart().addBasket(b);
        sys.reserveProducts();
        assertFalse(s.checkIfProductAvailable("blue top", 1));
        assertFalse(s.checkIfProductAvailable("blue top", 2));
        assertTrue(s.checkIfProductAvailable("skinny jeans", 1));
    }

    /*
    @Test
    public void appointOwner_Test(){
        sys.login("noy", false);
        sys.appointOwner("prince", "Pull&Bear");
        assertTrue(sys.getStoreByName("Pull&Bear").getOwnerships().containsKey(sys.getUserByName("prince")));
        sys.logout();
    }
     */

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
        assertFalse(sys.checkIfUserIsManager("Pull&Bear", "KING"));
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


    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void searchProducts_Test(){

        String output1 =sys.searchProducts("skinny jeans", "Clothing", null);
        assertEquals("Name: skinny jeans, Category: Clothing, Description: The most comfortable skiny jeans, Price: 120.0\n" +
                "Name: skinny jeans, Category: Clothing, Description: skinny jeans, Price: 100.0", output1);

        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("There are no products that match these parameters");
        sys.searchProducts("skirt", "Clothing", null);
    }

    @Test
    public void filterResults_Test(){
        sys.searchProducts("skinny jeans", "Clothing", null);
        String output1 =sys.filterResults(40, 200, "Clothing");
        assertEquals("Name: skinny jeans, Category: Clothing, Description: The most comfortable skiny jeans, Price: 120.0\n" +
                "Name: skinny jeans, Category: Clothing, Description: skinny jeans, Price: 100.0", output1);
        String output2 = sys.filterResults(40, 110, "Clothing");
        assertEquals("Name: skinny jeans, Category: Clothing, Description: skinny jeans, Price: 100.0", output2);
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("There are no products that match this search filter");
        sys.filterResults(40, 80, "Clothing");
    }

}
