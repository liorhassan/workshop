package ServiceLayer;

import DomainLayer.Security.SecurityHandler;
import DomainLayer.SystemHandler;

import java.security.Security;

public class Register {

    public String register(String username, String password){
        if (!SecurityHandler.getInstance().validPassword(password))
            return "Password isn't valid";
        try {
            SystemHandler.getInstance().register(username);
            SecurityHandler.getInstance().addUser(username, password);
            return "You have been successfully registered!";
        }
        catch (Exception e){
            return e.getMessage();
        }
    }
}
