package ServiceLayer;

import DomainLayer.Security.SecurityFacade;
import DomainLayer.TradingSystem.SystemFacade;
import DomainLayer.TradingSystem.SystemLogger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class UsersHandler {

    public String login(String username, String password, boolean mood){

        SystemLogger.getInstance().writeEvent("Login command: " + username);

        if (!SecurityFacade.getInstance().CorrectPassword(username, password)) {
            SystemLogger.getInstance().writeError("Invalid password");
            return createJSONMsg("ERROR", "This password is incorrect");
            //return "This password is incorrect";
        }
        try {
            String[] args ={username, password};
            if(SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("The username is invalid");
            if (! SystemFacade.getInstance().userExists(username))
                throw new IllegalArgumentException("This user is not registered");
            if(mood && !(SystemFacade.getInstance().checkIfUserIsAdmin(username)))
                throw new IllegalArgumentException("this user is not a system admin");
            SystemFacade.getInstance().login(username, mood);
            return createJSONMsg("SUCCESS", "You have been successfully logged in!");
            //return "You have been successfully logged in!";
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Login error: " + e.getMessage());
            return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }


    public String logout(){
        SystemLogger.getInstance().writeEvent("Logout command");
        return createJSONMsg("SUCCESS", SystemFacade.getInstance().logout());
        //return SystemFacade.getInstance().logout();
    }


    public String register(String username, String password){
        SystemLogger.getInstance().writeEvent("Register command: " + username);
        if (!SecurityFacade.getInstance().validPassword(password)) {
            SystemLogger.getInstance().writeError("Invalid password");
            return createJSONMsg("ERROR", "This password is not valid. Please choose a different one");
        }
        try {
            String[] args = {username};
            if (SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Username cannot be empty");
            if (SystemFacade.getInstance().userExists(username))
                throw new IllegalArgumentException("This username already exists in the system. Please choose a different one");
            SystemFacade.getInstance().register(username);
            SecurityFacade.getInstance().addUser(username, password);
            return createJSONMsg("SUCCESS", "You have been successfully registered!");
            //return "You have been successfully registered!";
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Register error: " + e.getMessage());
            return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }

    public void resetUsers(){
        SystemFacade.getInstance().resetUsers();
    }

    public void resetAdmins(){
        SystemFacade.getInstance().resetAdmins();

    }

    public String addAdmin(String username) {
        try {
            String[] args = {username};
            if (SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Username cannot be empty");
            if (!SystemFacade.getInstance().userExists(username))
                throw new IllegalArgumentException("This username not exist");
            if (SystemFacade.getInstance().checkIfUserIsAdmin(username))
                throw new IllegalArgumentException("This username is already admin");

            SystemFacade.getInstance().addAdmin(username);
            return createJSONMsg("SUCCESS", "done");
            //return"done";
        }
        catch (Exception e){
            return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }

    public String createJSONMsg(String type, String content) {
        JSONObject response = new JSONObject();
        response.put(type, content);
        return response.toJSONString();
    }

}

