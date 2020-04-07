package ServiceLayer;

import DomainLayer.SystemHandler;

public class ViewCart {
    public String execute(){
        return SystemHandler.getInstance().viewSoppingCart();
    }
}
