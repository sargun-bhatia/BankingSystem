import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AuthManager {
    private static final String AUTH_FILE = "users.dat";
    private List<User> users;

    public AuthManager() {
        users = new ArrayList<>();
        loadUsers();
        if (users.isEmpty()) createDefaultAdmin();
    }

    public boolean authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) return user;
        }
        return null;
    }

    public void createUser(String username, String password, String role) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                throw new IllegalArgumentException("User already exists!");
            }
        }
        users.add(new User(username, password, role));
        saveUsers();
    }

    private void createDefaultAdmin() {
        users.add(new User("admin", "admin123", "admin"));
        saveUsers();
    }

    private void loadUsers() {
        File file = new File(AUTH_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AUTH_FILE))) {
            users = (List<User>) ois.readObject();
        } catch (Exception e) {
            users = new ArrayList<>();
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AUTH_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
}
