package AcceptanceTests;

import DomainLayer.Permission;
import DomainLayer.SystemHandler;
import ServiceLayer.EditPermissions;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class UC4_6 {

    private EditPermissions editPermissions;

    @Before
    public void setUp(){
        this.editPermissions = new EditPermissions();
    }

    @BeforeClass
    public static void init(){
        // store owner
        SystemHandler.getInstance().register("noy");
        // store manager
        SystemHandler.getInstance().register("maor");
        SystemHandler.getInstance().login("noy");
        SystemHandler.getInstance().openNewStore("Mcdonalds", "The best hamburger in town");
    }

    @AfterClass
    public static void clean() {
        SystemHandler.getInstance().setUsers(new HashMap<>());
        SystemHandler.getInstance().setStores(new HashMap<>());
    }

    @Test
    public void valid() {
        List<Permission> p = new LinkedList<>();
        p.add(new Permission("View Store Purchase History"));
        String result = editPermissions.execute("maor", p, "Mcdonalds");
        assertEquals("Privileges have been edited successfully", result);
    }

    @Test
    public void userDoesntExist() {
        List<Permission> p = new LinkedList<>();
        p.add(new Permission("View Store Purchase History"));
        String result = editPermissions.execute("zuzu", p, "Mcdonalds");
        assertEquals("This username doesn't exist", result);
    }

    @Test
    public void storeDoesntExist() {
        List<Permission> p = new LinkedList<>();
        p.add(new Permission("View Store Purchase History"));
        String result = editPermissions.execute("maor", p, "Lalin");
        assertEquals("This store doesn't exists", result);
    }

    @Test
    public void userIsNotManager() {
        List<Permission> p = new LinkedList<>();
        p.add(new Permission("View Store Purchase History"));
        SystemHandler.getInstance().register("zuzu");
        String result = editPermissions.execute("zuzu", p, "Mcdonalds");
        assertEquals("You can't edit this user's privileges", result);
    }

    @Test
    public void emptyArg() {
        List<Permission> p = new LinkedList<>();
        String result = editPermissions.execute("maor", p, "Mcdonalds");
        assertEquals("Must enter username, permissions list and store name", result);

        p.add(new Permission("View Store Purchase History"));
        result = editPermissions.execute("maor", p, "");
        assertEquals("Must enter username, permissions list and store name", result);

        result = editPermissions.execute("", p, "Mcdonalds");
        assertEquals("Must enter username, permissions list and store name", result);
    }

    @Test
    public void userIsNotOwner() {
        SystemHandler.getInstance().logout();
        SystemHandler.getInstance().login("maor");

        List<Permission> p = new LinkedList<>();
        p.add(new Permission("View Store Purchase History"));
        String result = editPermissions.execute("noy", p, "Mcdonalds");
        assertEquals("You must be this store owner for this command", result);
    }

}
