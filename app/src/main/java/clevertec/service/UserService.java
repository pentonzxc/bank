package clevertec.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import clevertec.config.DatabaseConfig;
import clevertec.user.User;

public class UserService {

    public int create(User user) {
        String query = "INSERT INTO user_(first_name , last_name , birth_date) VALUES (? , ? , ?)";

        int id = 0;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getBirthDate());

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

    public boolean update(User user) {
        String query = """
                UPDATE user_ SET
                    first_name = ? , last_name = ?,
                    birth_date = ?
                WHERE id = ?
                """;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getLastName());
            ps.setInt(4, user.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<User> readAll() {
        List<User> users = null;
        String query = "select * from user_";

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

    public User read(int id) {
        String query = "select * from user_ where id = ?";
        User user = null;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query);) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setBirthDate(rs.getString("birth_date"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM user_ WHERE id = ?";

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
