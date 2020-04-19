package ServiceLayer;

import DomainLayer.Security.SecurityHandler;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

import java.security.Security;

public class Register {

    public String register(String username, String password){
        SystemLogger.getInstance().writeEvent("Register command: " + username);
        if (!SecurityHandler.getInstance().validPassword(password)) {
            SystemLogger.getInstance().writeError("Invalid password");
            return "This password is not valid. Please choose a different one";
        }
        try {
            SystemHandler.getInstance().register(username);
            SecurityHandler.getInstance().addUser(username, password);
            return "You have been successfully registered!";
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Register error: " + e.getMessage());
            return e.getMessage();
        }
    }
}
