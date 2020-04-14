package DomainLayer;

import DomainLayer.Security.SecurityHandler;

public class StoreOwning {

    public String login(String username, String storename){

        try {
            return SystemHandler.getInstance().appointManager(username, storename);
        }
        catch (Exception e){
            return e.getMessage();
        }
    }
}
