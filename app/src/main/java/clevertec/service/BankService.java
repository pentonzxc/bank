package clevertec.service;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import clevertec.Bank;
import clevertec.DatabaseConfig;

public class BankService {
    public int create(Bank bank) {
        String query = "INSERT INTO bank(name) VALUES (?)";

        int id = 0;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bank.getName());

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

    public boolean update(Bank bank) {
        String query = """
                UPDATE bank SET name = ? WHERE id = ?
                """;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, bank.getName());
            ps.setInt(2, bank.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Bank> readAll() {
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

    public Bank read(int id) {
        String query = "select * from bank where id = ?";
        Bank bank = null;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    bank = new Bank();
                    bank.setId(rs.getInt("id"));
                    bank.setName(rs.getString("name"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return bank;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM bank WHERE id = ?";

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
