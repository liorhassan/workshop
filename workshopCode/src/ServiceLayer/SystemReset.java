package ServiceLayer;

import DomainLayer.SystemHandler;

public class SystemReset {

    public void resetUsers(){
        SystemHandler.getInstance().resetUsers();
    }

    public void resetStores(){
        SystemHandler.getInstance().resetStores();
    }
}
