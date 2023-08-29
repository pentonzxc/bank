package clevertec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class FindAllService {

    /**
     * @return List<Bank>
     */
    public List<Bank> banks() {
        List<Bank> banks = null;
        String query = "select * from bank";

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            banks = new ArrayList<>();
            Bank bank;
            while (rs.next()) {
                bank = new Bank();
                bank.setId(rs.getInt("id"));
                bank.setName(rs.getString("name"));
                banks.add(bank);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return banks;
    }

    public List<User> users() {
        List<User> users = null;
        String query = "select * from user";

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            users = new ArrayList<>();
            User user;
            while (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setBirthDate(rs.getString("birth_date"));
                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @SuppressWarnings("unchecked")
    static public List<Account> accounts() {
        Object[] entityLists = allEntities();

        return (List<Account>) entityLists[0];
    }

    static public Object[] allEntities() {
        List<Account> accounts = null;
        HashMap<Integer, Bank> banks = null;
        HashMap<Integer, User> users = null;
        String accountQuery = "select * from account";

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement accountPs = con.prepareStatement(accountQuery);
                ResultSet accountRs = accountPs.executeQuery()) {
            accounts = new ArrayList<>();
            Account account;
            banks = new HashMap<>();
            users = new HashMap<>();

            while (accountRs.next()) {
                account = new Account();

                account.setId(accountRs.getInt("id"));
                account.setBalance(accountRs.getDouble("balance"));
                Integer bankId = (accountRs.getInt("bank_id"));
                Bank bankOpt = banks.getOrDefault(bankId, null);
                if (bankOpt != null) {
                    bankOpt.addAccount(account);
                    account.setBank(bankOpt);
                } else {

                    String bankQuery = "select * from bank where id = ?";

                    try (PreparedStatement bankPs = con.prepareStatement(bankQuery)) {
                        bankPs.setInt(1, bankId);

                        try (ResultSet bankRs = bankPs.executeQuery()) {
                            bankRs.next();

                            bankOpt = new Bank();
                            bankOpt.setId(bankRs.getInt("id"));
                            bankOpt.setName(bankRs.getString("name"));

                            bankOpt.addAccount(account);
                            banks.put(bankId, bankOpt);
                        }
                    }
                }

                Integer userId = accountRs.getInt("user_id");
                User userOpt = users.getOrDefault(userId, null);
                if (userOpt != null) {
                    userOpt.addAccount(account);
                } else {
                    String userQuery = "select * from user_ where id = ?";

                    try (PreparedStatement userPs = con.prepareStatement(userQuery)) {
                        userPs.setInt(1, userId);
                        try (ResultSet userRs = userPs.executeQuery()) {
                            userRs.next();

                            userOpt = new User();
                            userOpt.setId(userRs.getInt("id"));
                            userOpt.setFirstName(userRs.getString("first_name"));
                            userOpt.setLastName(userRs.getString("last_name"));
                            userOpt.setBirthDate(userRs.getString("birth_date"));

                            userOpt.addAccount(account);
                            users.put(userId, userOpt);
                        }
                    }
                }

                account.setBank(bankOpt);
                account.setUser(userOpt);

                accounts.add(account);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Collection<Bank> tmp1 = banks.values();
        List<Bank> banks_ = tmp1 instanceof List ? (List<Bank>) tmp1 : new ArrayList<Bank>(tmp1);
        Collection<User> tmp2 = users.values();
        List<User> users_ = tmp2 instanceof List ? (List<User>) tmp2 : new ArrayList<User>(tmp2);

        return new Object[] { accounts, banks_, users_ };
    }

    public User user(Integer id) {
        String query = "select * form user where user.id = ?";
        User user = null;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;

                user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setBirthDate(rs.getString("birth_date"));

                if (rs.next()) {
                    throw new RuntimeException("User with id :: " + id + ", more that one");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }
}
