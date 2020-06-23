package DomainLayer.Security;

import DataAccessLayer.PersistenceController;

public class SecurityFacade {
    private static SecurityFacade ourInstance = new SecurityFacade();

    public static SecurityFacade getInstance() {
        return ourInstance;
    }

    private SecurityFacade() {
    }

    public boolean addUser(String username, String password){
        UserDetails userDetails = new UserDetails(username, encrypt(password));
        PersistenceController.create(userDetails);

        return true;
    }

    public boolean validPassword(String password){
        for (Character c: password.toCharArray()) {
            if (!valid(c))
                return false;
        }
        return true;
    }

    public boolean CorrectPassword (String username, String password) {
//        if(password.equals("1234") || password.equals("good") || password.equals("951") || password.equals("toya") || password.equals("tester") || password.equals("shauli"))
//            return true;
//        return false;
        // get details from database
        return PersistenceController.readUserDetails(username).checkIfCorrectPassword(encrypt(password));
    }

    private boolean valid(Character c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }


    private String decrypt(String secret) {
        StringBuilder tmp = new StringBuilder();
        final int OFFSET = 4;
        for (int i = 0; i < secret.length(); i++) {
            tmp.append(secret.charAt(i) - OFFSET);
        }

        String reversed = new StringBuffer(tmp.toString()).reverse().toString();
        return reversed;
    }

    private String encrypt(String password) {
//        String b64encoded = Base64.getEncoder().encode(ip);

        // Reverse the string
        String reverse = new StringBuffer(password).reverse().toString();

        StringBuilder tmp = new StringBuilder();
        final int OFFSET = 4;
        for (int i = 0; i < reverse.length(); i++) {
            tmp.append(reverse.charAt(i) + OFFSET);
        }
        return tmp.toString();
    }
}
