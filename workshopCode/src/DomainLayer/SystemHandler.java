package DomainLayer;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SystemHandler {
    private static SystemHandler ourInstance = new SystemHandler();
    public static SystemHandler getInstance() {
        return ourInstance;
    }

    private User activeUser;
    private boolean adminMode;
    private HashMap<String,User> usersMap;
    private HashMap<String,Store> storeMap;
    private List<User> adminsList;


    private SystemHandler() {
        usersMap = new HashMap<>();
        storeMap = new HashMap<>();
        adminsList = new ArrayList<>();
        activeUser = null;
    }

    public User getUserByName(String username){
        return usersMap.get(username);
    }

    public Store getStoreByName(String storeName){
        return storeMap.get(storeName);
    }

    public User getActiveUser(){
        return activeUser;
    }



}
