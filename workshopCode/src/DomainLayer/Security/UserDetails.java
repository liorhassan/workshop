package DomainLayer.Security;


import DomainLayer.TradingSystem.Models.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "usersDetails")
public class UserDetails implements Serializable {

    @Id
    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "encodedPassword")
    private String encodedPassword;

    public UserDetails() {}
    public UserDetails(String username, String password) {
        this.username = username;
        this.encodedPassword = password;
    }

    public boolean checkIfCorrectPassword(String cipherPass) {
        return cipherPass.equals(this.encodedPassword);
    }

}
