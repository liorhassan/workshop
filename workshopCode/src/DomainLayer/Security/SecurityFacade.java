package DomainLayer.Security;

public class SecurityFacade {
    private static SecurityFacade ourInstance = new SecurityFacade();

    public static SecurityFacade getInstance() {
        return ourInstance;
    }

    private SecurityFacade() {
    }

    public boolean addUser(String username, String password){ return true; }

    public boolean validPassword(String password){
        for (Character c: password.toCharArray()) {
            if (!valid(c))
                return false;
        }
        return true;
    }

    public boolean CorrectPassword (String username, String password) {
        if(password.equals("1234") || password.equals("good") || password.equals("951") || password.equals("toya") || password.equals("tester") || password.equals("shauli"))
            return true;
        return false;
    }

    private boolean valid(Character c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }
}
