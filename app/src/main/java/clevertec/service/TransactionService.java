package clevertec.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import clevertec.Account;
import clevertec.DatabaseConfig;
import clevertec.transaction.check.TransactionCheck;

public class TransactionService {

    AccountService accountService = new AccountService();

    public int create(TransactionCheck check) {
        String query = "INSERT INTO transaction_(finished_date, origin_account_id , target_account_id) VALUES (? , ? , ? , ?)";

        int id = 0;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(check.getCreatedAt()));
            ps.setInt(2, check.getOrigin().getId());
            ps.setInt(3, check.getTarget().getId());
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

    public boolean update(TransactionCheck check) {
        String query = """
                UPDATE transaction_ SET finished_date = ?, origin_account_id = ?, target_account_id = ? WHERE ID = ?
                """;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setTimestamp(1, Timestamp.valueOf(check.getCreatedAt()));
            ps.setInt(2, check.getOrigin().getId());
            ps.setInt(3, check.getTarget().getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public TransactionCheck read(UUID id) {

        String query = "select * from transaction_ where id = ?";
        TransactionCheck check = null;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query);) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    check = new TransactionCheck();
                    check.setId(rs.getObject("id", UUID.class));
                    check.setCreatedAt(rs.getTimestamp("finished_date").toLocalDateTime());
                    check.setOrigin(accountService.read(rs.getInt("origin_account_id")));

                    check.setTarget(accountService.read(rs.getInt("target_account_id")));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return check;
    }

    public List<TransactionCheck> readAllWhereOriginAccountId(int id) {
        HashMap<Integer, Account> accounts = new HashMap<>();

        String query = "select * from transaction_ where origin_account_id = ?";
        List<TransactionCheck> checks = null;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query);) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                checks = new ArrayList<>();
                TransactionCheck check;
                while (rs.next()) {
                    Account origin;
                    Account target;

                    check = new TransactionCheck();
                    check.setId(rs.getObject("id", UUID.class));
                    check.setCreatedAt(rs.getTimestamp("finished_date").toLocalDateTime());
                    int origin_account_id = rs.getInt("origin_account_id");
                    int target_account_id = rs.getInt("target_account_id");
                    origin = accounts.getOrDefault(origin_account_id, null);
                    if (origin == null) {
                        origin = accountService.read(origin_account_id);
                        accounts.put(origin.getId(), origin);
                    }
                    target = accounts.getOrDefault(target_account_id, null);
                    if (target == null) {
                        target = accountService.read(target_account_id);
                        accounts.put(target.getId(), target);
                    }

                    check.setOrigin(origin);
                    check.setTarget(target);

                    checks.add(check);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return checks;
    }

    public List<TransactionCheck> readAllWhereTargetAccountId(int id) {
        HashMap<Integer, Account> accounts = new HashMap<>();

        String query = "select * from transaction_ where target_account_id = ?";
        List<TransactionCheck> checks = null;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query);) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                checks = new ArrayList<>();
                TransactionCheck check;
                while (rs.next()) {
                    Account origin;
                    Account target;

                    check = new TransactionCheck();
                    check.setId(rs.getObject("id", UUID.class));
                    check.setCreatedAt(rs.getTimestamp("finished_date").toLocalDateTime());
                    int origin_account_id = rs.getInt("origin_account_id");
                    int target_account_id = rs.getInt("target_account_id");
                    origin = accounts.getOrDefault(origin_account_id, null);
                    if (origin == null) {
                        origin = accountService.read(origin_account_id);
                        accounts.put(origin.getId(), origin);
                    }
                    target = accounts.getOrDefault(target_account_id, null);
                    if (target == null) {
                        target = accountService.read(target_account_id);
                        accounts.put(target.getId(), target);
                    }

                    check.setOrigin(origin);
                    check.setTarget(target);
                    checks.add(check);

                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return checks;
    }

    public List<TransactionCheck> readAll() {
        HashMap<Integer, Account> accounts = new HashMap<>();

        String query = "select * from transaction_";
        List<TransactionCheck> checks = null;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            checks = new ArrayList<>();
            TransactionCheck check;
            while (rs.next()) {
                Account origin;
                Account target;

                check = new TransactionCheck();
                check.setId(rs.getObject("id", UUID.class));
                check.setCreatedAt(rs.getTimestamp("finished_date").toLocalDateTime());
                int origin_account_id = rs.getInt("origin_account_id");
                int target_account_id = rs.getInt("target_account_id");
                origin = accounts.getOrDefault(origin_account_id, null);
                if (origin == null) {
                    origin = accountService.read(origin_account_id);
                    accounts.put(origin.getId(), origin);
                }
                target = accounts.getOrDefault(target_account_id, null);
                if (target == null) {
                    target = accountService.read(target_account_id);
                    accounts.put(target.getId(), target);
                }

                check.setOrigin(origin);
                check.setTarget(target);
                checks.add(check);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return checks;
    }

    public boolean delete(UUID id) {
        String query = "DELETE FROM transaction_ WHERE id = ?";

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setObject(1, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
