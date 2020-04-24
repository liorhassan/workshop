package ServiceLayer;

import DomainLayer.Security.SecurityHandler;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

public class UsersHandler {

    public String login(String username, String password){
        if (!SecurityHandler.getInstance().CorrectPassword(username, password))
            return "This password is incorrect";
        try {
            SystemHandler.getInstance().login(username);
            return "You have been successfully logged in!";
        }
        catch (Exception e){
            return e.getMessage();
        }
    }

    public String logout(){
        SystemLogger.getInstance().writeEvent("Logout command");
        return SystemHandler.getInstance().logout();
    }


    public String register(String username, String password){
        SystemLogger.getInstance().writeEvent("Register command: " + username);
        if (!SecurityHandler.getInstance().validPassword(password)) {
            SystemLogger.getInstance().writeError("Invalid password");
            return "This password is not valid. Please choose a different one";
        }
        try {
            String[] args = {username};
            if (SystemHandler.getInstance().emptyString(args))
                throw new IllegalArgumentException("Username cannot be empty");
            if (SystemHandler.getInstance().userExists(username))
                throw new IllegalArgumentException("This username already exists in the system. Please choose a different one");
            SystemHandler.getInstance().register(username);
            SecurityHandler.getInstance().addUser(username, password);
            return "You have been successfully registered!";
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Register error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public void resetUsers(){
        SystemHandler.getInstance().resetUsers();
    }


}

