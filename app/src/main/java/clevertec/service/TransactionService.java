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

import clevertec.account.Account;
import clevertec.config.DatabaseConfig;
import clevertec.transaction.check.TransactionDescription;
import clevertec.transaction.check.TransactionCheck;

public class TransactionService {

    AccountService accountService;

    public TransactionService() {
        this.accountService = new AccountService();
    }

    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }

    public UUID create(TransactionCheck check) {
        String query = "INSERT INTO transaction_(transfer_amount, description, created_at, origin_account_id , target_account_id) VALUES (? , ? , ?, ? , ?)";

        UUID id = null;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, check.getTransferAmount());
            ps.setString(2, check.getDescription().description());
            ps.setTimestamp(3, Timestamp.valueOf(check.getCreatedAt()));
            ps.setInt(4, check.getOrigin().getId());
            ps.setInt(5, check.getTarget().getId());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getObject(1, UUID.class);
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
                UPDATE transaction_ SET transfer_amount = ?, description = ?, created_at = ?, origin_account_id = ?, target_account_id = ? WHERE ID = ?
                """;

        try (Connection con = DatabaseConfig.getConnecion();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDouble(1, check.getTransferAmount());
            ps.setString(2, check.getDescription().description());
            ps.setTimestamp(3, Timestamp.valueOf(check.getCreatedAt()));
            ps.setInt(4, check.getOrigin().getId());
            ps.setInt(5, check.getTarget().getId());

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
                    check.setTransferAmount(rs.getInt("transfer_amount"));
                    check.setDescription(
                            TransactionDescription.fromDescription(rs.getString("description")).get());
                    check.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
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
                    check.setTransferAmount(rs.getInt("transfer_amount"));
                    check.setDescription(
                            TransactionDescription.fromDescription(rs.getString("description")).get());
                    check.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
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
                    check.setTransferAmount(rs.getInt("transfer_amount"));
                    check.setDescription(
                            TransactionDescription.fromDescription(rs.getString("description")).get());
                    check.setId(rs.getObject("id", UUID.class));
                    check.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
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
                check.setTransferAmount(rs.getInt("transfer_amount"));
                check.setDescription(
                        TransactionDescription.fromDescription(rs.getString("description")).get());
                check.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
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
