import java.util.ArrayList;
import java.util.List;

public class BankManager {
    private List<Account> accounts;
    private FileHandler fileHandler;

    public BankManager() {
        accounts = new ArrayList<>();
        fileHandler = new FileHandler();
        loadAccounts();
    }

    public BankManager(User currentUser) {
        this();
    }

    public void createAccount(String accountNumber, String accountHolderName, String accountType) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(accountNumber)) {
                throw new IllegalArgumentException("Account number already exists!");
            }
        }
        Account account = new Account(accountNumber, accountHolderName, accountType);
        accounts.add(account);
        saveAccounts();
    }

    public void deposit(String accountNumber, double amount) {
        Account account = findAccount(accountNumber);
        if (account != null && "Active".equals(account.getStatus())) {
            account.deposit(amount);
            saveAccounts();
        } else {
            throw new IllegalArgumentException("Account not found or inactive!");
        }
    }

    public boolean withdraw(String accountNumber, double amount) {
        Account account = findAccount(accountNumber);
        if (account != null && "Active".equals(account.getStatus())) {
            boolean success = account.withdraw(amount);
            if (success) saveAccounts();
            return success;
        }
        throw new IllegalArgumentException("Account not found or inactive!");
    }

    public void transfer(String fromAccount, String toAccount, double amount) {
        Account fromAcc = findAccount(fromAccount);
        Account toAcc = findAccount(toAccount);
        if (fromAcc == null || toAcc == null) {
            throw new IllegalArgumentException("One or both accounts not found!");
        }
        if (!"Active".equals(fromAcc.getStatus()) || !"Active".equals(toAcc.getStatus())) {
            throw new IllegalArgumentException("One or both accounts are inactive!");
        }
        if (fromAcc.withdraw(amount)) {
            toAcc.deposit(amount);
            saveAccounts();
        } else {
            throw new IllegalArgumentException("Insufficient balance!");
        }
    }

    public void deleteAccount(String accountNumber) {
        Account account = findAccount(accountNumber);
        if (account != null) {
            account.setStatus("Closed");
            saveAccounts();
        } else {
            throw new IllegalArgumentException("Account not found!");
        }
    }

    public Account findAccount(String accountNumber) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(accountNumber)) return acc;
        }
        return null;
    }

    public List<Account> searchAccounts(String searchTerm) {
        List<Account> results = new ArrayList<>();
        for (Account acc : accounts) {
            if (acc.getAccountNumber().contains(searchTerm) || 
                acc.getAccountHolderName().toLowerCase().contains(searchTerm.toLowerCase())) {
                results.add(acc);
            }
        }
        return results;
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts);
    }

    public double getBalance(String accountNumber) {
        Account account = findAccount(accountNumber);
        return account != null ? account.getBalance() : 0.0;
    }

    public void updateAccount(String accountNumber, String name, String type) {
        Account account = findAccount(accountNumber);
        if (account != null) {
            account.setAccountHolderName(name);
            account.setAccountType(type);
            saveAccounts();
        }
    }

    private void loadAccounts() {
        accounts = fileHandler.loadAccounts();
    }

    private void saveAccounts() {
        fileHandler.saveAccounts(accounts);
    }
}
