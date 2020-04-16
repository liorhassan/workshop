package AcceptanceTests;

import DomainLayer.*;
import ServiceLayer.RemoveStoreManager;
import ServiceLayer.UpdateInventory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UC4_7 {
    private RemoveStoreManager command;

    @Before
    public void setUp() throws Exception{
        command = new RemoveStoreManager();
    }

    @BeforeClass
    public static void init() throws Exception{
        Store s = new Store("FoxHome", "stuff for home", SystemHandler.getInstance().getActiveUser(), new StoreOwning());
        SystemHandler.getInstance().getStores().put("FoxHome", s);
        SystemHandler.getInstance().register("shauli");
        SystemHandler.getInstance().register("toya");
        SystemHandler.getInstance().appointManager("toya","FoxHome");
    }

    @Test
    public void valid(){
        String result = command.execute("toya","FoxHome");
        assertEquals("Manager removed successfully!", result);
        SystemHandler.getInstance().appointManager("toya","FoxHome");
    }

    @Test
    public void storeDoesNotExists(){
        String result = command.execute("toya","Fox");
        assertEquals("This store doesn't exist", result);
    }

    @Test
    public void userDoesNotExists(){
        String result = command.execute("cooper","FoxHome");
        assertEquals("This username doesn't exist", result);
    }

    @Test
    public void doesNotHavePrivileges(){
        Store s2 = new Store("Castro", "clothing", new User(), new StoreOwning());
        SystemHandler.getInstance().getStores().put("Castro", s2);
        String result = command.execute("toya", "Castro");
        assertEquals("You must be this store owner for this command", result);
    }

    @Test
    public void notAppointedByUser(){
        String result = command.execute("shauli","FoxHome");
        assertEquals("This username is not one of this store's managers appointed by you", result);
    }

    @Test
    public void emptyInput(){
        String result = command.execute("","FoxHome");
        assertEquals("Must enter username and store name", result);
        result = command.execute(null,"FoxHome");
        assertEquals("Must enter username and store name", result);
        result = command.execute("toya","");
        assertEquals("Must enter username and store name", result);
        result = command.execute("toya",null);
        assertEquals("Must enter username and store name", result);
    }

}
