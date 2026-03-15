import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BankGUI extends JFrame {
    private BankManager bankManager;
    private User currentUser;
    
    // GUI Components
    private JTextField accNumberField, accNameField, searchField, amountField, fromAccField, toAccField;
    private JComboBox<String> accTypeCombo;
    private JTable accountsTable;
    private DefaultTableModel tableModel;
    private JButton createBtn, updateBtn, deleteBtn, clearBtn;
    private JButton depositBtn, withdrawBtn, transferBtn, balanceBtn, searchBtn, refreshBtn;

    public BankGUI(User user) {
        this.currentUser = user;
        bankManager = new BankManager();
        initializeGUI();
        loadAccountsTable();
        setupRoleBasedPermissions();
        setVisible(true);
    }

    private void initializeGUI() {
        setTitle("Bank Management System - " + (currentUser.isAdmin() ? "Admin" : "User"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.add(new JLabel("BANK MANAGEMENT SYSTEM", JLabel.CENTER));
        titlePanel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createLeftPanel());
        splitPane.setRightComponent(createRightPanel());
        splitPane.setDividerLocation(400);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Account Management"));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.add(new JLabel("Account Number:"));
        accNumberField = new JTextField();
        formPanel.add(accNumberField);

        formPanel.add(new JLabel("Account Holder:"));
        accNameField = new JTextField();
        formPanel.add(accNameField);

        formPanel.add(new JLabel("Account Type:"));
        accTypeCombo = new JComboBox<>(new String[]{"Savings", "Current"});
        formPanel.add(accTypeCombo);

        createBtn = new JButton("Create Account");
        updateBtn = new JButton("Update Account");
        deleteBtn = new JButton("Delete Account");
        clearBtn = new JButton("Clear Fields");

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
        buttonPanel.add(createBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        formPanel.add(new JLabel());
        formPanel.add(buttonPanel);
        leftPanel.add(formPanel, BorderLayout.CENTER);

        setupLeftPanelListeners();
        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        String[] columns = {"Account No", "Name", "Type", "Balance", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        accountsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel transPanel = new JPanel(new GridLayout(5, 1));
        transPanel.setBorder(BorderFactory.createTitledBorder("Transactions"));

        amountField = new JTextField();
        depositBtn = new JButton("Deposit");
        withdrawBtn = new JButton("Withdraw");
        transferBtn = new JButton("Transfer");
        balanceBtn = new JButton("Check Balance");
        refreshBtn = new JButton("Refresh");

        transPanel.add(amountField);
        transPanel.add(depositBtn);
        transPanel.add(withdrawBtn);
        transPanel.add(transferBtn);
        transPanel.add(balanceBtn);
        transPanel.add(refreshBtn);

        rightPanel.add(transPanel, BorderLayout.SOUTH);
        searchBtn = new JButton("Search");
        setupRightPanelListeners();
        return rightPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        
        searchField = new JTextField();
        searchBtn = new JButton("Search");
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);
        bottomPanel.add(searchPanel);
        return bottomPanel;
    }

    private void setupLeftPanelListeners() {
        createBtn.addActionListener(e -> createAccount());
        updateBtn.addActionListener(e -> updateAccount());
        deleteBtn.addActionListener(e -> deleteAccount());
        clearBtn.addActionListener(e -> clearFields());
    }

    private void setupRightPanelListeners() {
        depositBtn.addActionListener(e -> performDeposit());
        withdrawBtn.addActionListener(e -> performWithdraw());
        transferBtn.addActionListener(e -> performTransfer());
        balanceBtn.addActionListener(e -> checkBalance());
        refreshBtn.addActionListener(e -> loadAccountsTable());
        searchBtn.addActionListener(e -> searchAccounts());
    }

    private void setupRoleBasedPermissions() {
        if (!currentUser.isAdmin()) {
            deleteBtn.setEnabled(false);
            updateBtn.setEnabled(false);
            deleteBtn.setToolTipText("Admin only");
            updateBtn.setToolTipText("Admin only");
        }
    }

    private void createAccount() {
        try {
            String accNum = accNumberField.getText().trim();
            String name = accNameField.getText().trim();
            String type = (String) accTypeCombo.getSelectedItem();
            if (accNum.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            bankManager.createAccount(accNum, name, type);
            JOptionPane.showMessageDialog(this, "Account created!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAccountsTable();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAccount() {
        try {
            String accNum = accNumberField.getText().trim();
            String name = accNameField.getText().trim();
            String type = (String) accTypeCombo.getSelectedItem();
            bankManager.updateAccount(accNum, name, type);
            JOptionPane.showMessageDialog(this, "Account updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAccountsTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAccount() {
        String accNum = accNumberField.getText().trim();
        if (accNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter account number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Close account " + accNum + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bankManager.deleteAccount(accNum);
                JOptionPane.showMessageDialog(this, "Account closed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAccountsTable();
                clearFields();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        accNumberField.setText("");
        accNameField.setText("");
        accTypeCombo.setSelectedIndex(0);
    }

    private void performDeposit() {
        try {
            String accNum = getSelectedAccountNumber();
            double amount = Double.parseDouble(amountField.getText().trim());
            bankManager.deposit(accNum, amount);
            JOptionPane.showMessageDialog(this, "Deposit successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAccountsTable();
            amountField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performWithdraw() {
        try {
            String accNum = getSelectedAccountNumber();
            double amount = Double.parseDouble(amountField.getText().trim());
            if (bankManager.withdraw(accNum, amount)) {
                JOptionPane.showMessageDialog(this, "Withdrawal successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAccountsTable();
                amountField.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performTransfer() {
        JOptionPane.showMessageDialog(this, "Transfer: Use account fields for from/to accounts", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void checkBalance() {
        String accNum = getSelectedAccountNumber();
        if (!accNum.isEmpty()) {
            double balance = bankManager.getBalance(accNum);
            JOptionPane.showMessageDialog(this, "Balance: $" + String.format("%.2f", balance), "Balance", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchAccounts() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAccountsTable();
        } else {
            List<Account> results = bankManager.searchAccounts(searchTerm);
            updateTable(results);
        }
    }

    private String getSelectedAccountNumber() {
        int row = accountsTable.getSelectedRow();
        return row >= 0 ? tableModel.getValueAt(row, 0).toString() : "";
    }

    private void loadAccountsTable() {
        updateTable(bankManager.getAllAccounts());
    }

    private void updateTable(List<Account> accounts) {
        tableModel.setRowCount(0);
        for (Account acc : accounts) {
            tableModel.addRow(new Object[]{
                acc.getAccountNumber(),
                acc.getAccountHolderName(),
                acc.getAccountType(),
                String.format("%.2f", acc.getBalance()),
                acc.getStatus()
            });
        }
    }
}
