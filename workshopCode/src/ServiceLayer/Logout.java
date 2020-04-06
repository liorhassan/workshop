package ServiceLayer;

import DomainLayer.SystemHandler;

public class Logout {

    public String execute(){
        return SystemHandler.getInstance().logout();
    }
}
