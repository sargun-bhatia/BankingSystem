import javax.swing.*;
import java.awt.*;

public class LoginGUI extends JFrame {
    private AuthManager authManager;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginGUI() {
        authManager = new AuthManager();
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Bank Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("BANK MANAGEMENT SYSTEM", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        mainPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register New User");
        
        loginBtn.addActionListener(e -> attemptLogin());
        registerBtn.addActionListener(e -> showRegisterDialog());
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        mainPanel.add(buttonPanel, gbc);

        gbc.gridy = 4;
        JLabel demoLabel = new JLabel("Demo: admin / admin123", JLabel.CENTER);
        demoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        mainPanel.add(demoLabel, gbc);

        add(mainPanel);
        setVisible(true);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password!", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authManager.authenticate(username, password)) {
            User currentUser = authManager.getUser(username);
            new BankGUI(currentUser);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private void showRegisterDialog() {
        JTextField newUserField = new JTextField(10);
        JPasswordField newPassField = new JPasswordField(10);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"user", "admin"});

        Object[] fields = {
            "New Username:", newUserField,
            "New Password:", newPassField,
            "Role:", roleCombo
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Register New User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newUser = newUserField.getText().trim();
            String newPass = new String(newPassField.getPassword()).trim();
            String role = (String) roleCombo.getSelectedItem();

            if (!newUser.isEmpty() && !newPass.isEmpty()) {
                try {
                    authManager.createUser(newUser, newPass, role);
                    JOptionPane.showMessageDialog(this, "User created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
