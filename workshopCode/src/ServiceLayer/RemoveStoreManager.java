package ServiceLayer;

import DomainLayer.SystemHandler;

import java.util.concurrent.ExecutionException;

public class RemoveStoreManager {
    public String execute(String username,String storename){
        try{
            return SystemHandler.getInstance().removeManager(username,storename);
        } catch(Exception e) {
            return e.getMessage();
        }
    }
}
