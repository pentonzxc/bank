package clevertec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FindAllService {

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
                bank.setId(rs.getString("id"));
                bank.setName(rs.getString("name"));
                banks.add(bank);
            }

        } catch (Exception e) {
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
                user.setId(rs.getString("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setBirthDate(rs.getString("birth_date"));
                users.add(user);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public List<User> accounts() {
        return null;
    }

    public User user(String id) {
        return null;
    }

    public User user(String id, Connection con) throws SQLException {
        String query = "select * form user where user.id = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, id);
        } finally {

        }

        return null;
    }
}
