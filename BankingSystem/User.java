import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User() {}

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public boolean isAdmin() { return "admin".equals(role); }
    public boolean isUser() { return "user".equals(role); }
}
