package DomainLayer.Security;

public class SecurityHandler {
    private static SecurityHandler ourInstance = new SecurityHandler();

    public static SecurityHandler getInstance() {
        return ourInstance;
    }

    private SecurityHandler() {
    }

    public boolean addUser(String username, String password){
        return true;
    }

    public boolean validPassword(String password){return true;}
    public  boolean CorrectPassword (String username, String password) {return true;}

}
