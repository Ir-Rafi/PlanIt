import java.io.Serializable;

public class User implements Serializable {
    public String username;
    public String role;

    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }
}