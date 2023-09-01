package clevertec.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import clevertec.account.Account;
import clevertec.bank.Bank;
import clevertec.config.DatabaseConfig;
import clevertec.transaction.Transaction;
import clevertec.transaction.check.TransactionCheck;
import clevertec.user.User;

public class AccountService {

    BankService bankService;

    UserService userService;

    public AccountService() {
        this.bankService = new BankService();
        this.userService = new UserService();
    }

    public AccountService(BankService bankService, UserService userService) {
        this.bankService = bankService;
        this.userService = userService;
    }

    public int create(Account account) {
        String query = "INSERT INTO account(account_number, balance ,currency , opening_date, bank_id, user_id) VALUES (? , ? , ? , ? , ? , ?)";

        int id = 0;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getAccountNumber());
            ps.setDouble(2, account.getBalance());
            ps.setString(3, account.getCurrency());
            ps.setTimestamp(4, Timestamp.valueOf(account.getOpeningDate()));
            ps.setInt(5, account.getBank().getId());
            ps.setInt(6, account.getUser().getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return id;
    }

    public boolean update(Account account) {
        String query = """
                UPDATE account SET account_number = ?, balance = ?, currency = ?, opening_date = ?,
                bank_id = ?, user_id = ? WHERE id = ?
                """;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, account.getAccountNumber());
            ps.setDouble(2, account.getBalance());
            ps.setString(3, account.getCurrency());
            ps.setTimestamp(4, null);
            ps.setInt(5, account.getBank().getId());
            ps.setInt(6, account.getUser().getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Account read(int id) {
        String query = "select * from account where id = ?";
        Account account = null;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query);) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    account = new Account();
                    account.setId(rs.getInt("id"));
                    account.setAccountNumber(rs.getString("account_number"));
                    account.setBalance(rs.getDouble("balance"));
                    account.setOpeningDate(rs.getTimestamp("opening_date").toLocalDateTime());
                    account.setCurrency(rs.getString("currency"));

                    account.setBank(bankService.read(rs.getInt("bank_id")));
                    account.setUser(userService.read(rs.getInt("user_id")));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return account;
    }

    public List<Account> readAll() {
        List<Account> accounts = null;
        String query = "select * from account";
        HashMap<Integer, Bank> banks = new HashMap<>();
        HashMap<Integer, User> users = new HashMap<>();

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            accounts = new ArrayList<>();
            Account account;
            while (rs.next()) {
                Bank bank;
                User user;
                account = new Account();
                account.setId(rs.getInt("id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setBalance(rs.getDouble("balance"));
                account.setOpeningDate(rs.getTimestamp("opening_date").toLocalDateTime());
                account.setCurrency(rs.getString("currency"));

                int bank_id = rs.getInt("bank_id");
                int user_id = rs.getInt("user_id");

                bank = banks.getOrDefault(bank_id, null);
                user = users.getOrDefault(user_id, null);

                if (bank == null) {
                    bank = bankService.read(bank_id);
                }
                if (user == null) {
                    user = userService.read(user_id);
                }

                account.setBank(bank);
                account.setUser(user);
                accounts.add(account);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return accounts;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM account WHERE id = ?";

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

}
