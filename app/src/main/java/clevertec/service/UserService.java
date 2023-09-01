package clevertec.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import clevertec.account.Account;
import clevertec.bank.Bank;
import clevertec.config.DatabaseConfig;
import clevertec.transaction.check.TransactionDescription;
import clevertec.user.User;
import clevertec.transaction.check.TransactionCheck;
import clevertec.util.DateUtil;
import clevertec.util.MoneyUtil;

public class UserService {

    // public File generateBankAccountsStatement(Account ) {

    // return null;
    // }

    static DateTimeFormatter ddMMYYYYFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    static DateTimeFormatter ddMMYYYYHHmmFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH.mm");

    public int create(User user) {
        String query = "INSERT INTO user_(first_name , last_name) VALUES (? , ?)";

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
                UPDATE user_ SET first_name = ? , last_name = ?, birth_date = ? WHERE id = ?
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

    public static File generateBankAccountStatement(Account acc, LocalDate from, LocalDate end) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String fileName = acc.getId() + "|" + DateUtil.dateTimeToStringWithoutSeconds(now);
        File file = new File(fileName + ".pdf");

        // DateTimeFormatter ddMMYYYYFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        // DateTimeFormatter ddMMYYYYHHmmFormat =
        // DateTimeFormatter.ofPattern("dd.MM.yyyy, HH.mm");

        PDDocument pdf = new PDDocument();
        pdf.addPage(new PDPage());
        PDPage page = pdf.getPage(0);
        PDFont font = PDType1Font.HELVETICA_BOLD;
        int fontSize = 12;
        PDPageContentStream stream = new PDPageContentStream(
                pdf,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true,
                true);
        stream.setFont(font, fontSize);

        Function<String, Float> measure = str -> {
            try {
                return font.getStringWidth(str) / 1000 * fontSize - 6;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        String header = "Bank statement";

        String bankLabel = "Bank";
        String clientLabel = "Client";
        String accountLabel = "Account";
        String currencyLabel = "Currency";
        String openingDateLabel = "Opening date";
        String periodLabel = "Period";
        String createdAtLabel = "Date and time of creation";
        String balanceLabel = "Balance";

        float startX = 50;
        float curY = page.getMediaBox().getUpperRightY() - 50;
        float dy = 20;
        String separator = "|";
        float separatorGap = 10;
        float width = page.getMediaBox().getWidth();

        // Generate header

        curY = generateHeader(
                header,
                stream,
                (width / 2) - measure.apply(header.substring(0, header.length() / 2)),
                curY,
                dy);

        Function<Account, Bank> g1 = Account::getBank;
        Function<Bank, String> f1 = Bank::getName;

        // generate bank row

        curY = generateRow(
                bankLabel,
                acc,
                f1.compose(g1),
                stream,
                startX,
                curY,
                width,
                separator,
                separatorGap,
                dy,
                measure);

        Function<Account, User> g2 = Account::getUser;
        Function<User, String> f2 = user -> user.getFirstName() + " " + user.getLastName();

        // generate client row

        curY = generateRow(
                clientLabel,
                acc,
                f2.compose(g2),
                stream,
                startX,
                curY,
                width,
                separator,
                separatorGap,
                dy,
                measure);

        // generate account row

        curY = generateRow(
                accountLabel,
                acc,
                Account::getAccountNumber,
                stream,
                startX,
                curY,
                width,
                separator,
                separatorGap,
                dy,
                measure);

        // generate currency row

        curY = generateRow(
                currencyLabel,
                acc,
                Account::getCurrency,
                stream,
                startX,
                curY,
                width,
                separator,
                separatorGap,
                dy,
                measure);

        // generate opening date row

        Function<LocalDateTime, String> f3 = d -> d.format(ddMMYYYYFormat);
        Function<Account, LocalDateTime> g3 = Account::getOpeningDate;

        curY = generateRow(
                openingDateLabel,
                acc,
                f3.compose(g3),
                stream,
                startX,
                curY,
                width,
                separator,
                separatorGap,
                dy,
                measure);

        // generate period row

        String period = from.format(ddMMYYYYFormat) + " - " + end.format(ddMMYYYYFormat);

        curY = generateRow(
                periodLabel,
                period,
                Function.identity(),
                stream,
                startX,
                curY,
                width,
                separator,
                separatorGap,
                dy,
                measure);

        // generate createdAt row

        Function<LocalDateTime, String> f4 = d -> d.format(ddMMYYYYHHmmFormat);

        curY = generateRow(
                createdAtLabel,
                now,
                f4,
                stream,
                startX,
                curY,
                width,
                separator,
                separatorGap,
                dy,
                measure);

        // generate balance row

        Function<Double, String> f5 = balance -> balance + " " + acc.getCurrency();
        Function<Double, Double> g5 = MoneyUtil::roundMoney;
        Function<Account, Double> z5 = Account::getBalance;

        curY = generateRow(
                balanceLabel,
                acc,
                f5.compose(g5.compose(z5)),
                stream,
                startX,
                curY,
                width,
                separator,
                separatorGap,
                dy,
                measure);

        generateContent(acc, stream, measure, from, end, curY);

        stream.close(); // don't forget that one!

        pdf.save(file);

        return null;
    }

    private static void generateContent(
            Account acc,
            PDPageContentStream stream,
            Function<String, Float> measure,
            LocalDate from,
            LocalDate to,
            float y) throws IOException {
        float startX = 50;
        float curY = y;
        float gap = 30;
        float dy = 20;
        float dateWordLength = measure.apply("01.01.1970");

        float gapNotesLabel = gap + 50;
        float gapTransferLabel = gap + 150;

        String separator = "|";
        String dateLabel = "Date";
        String notesLabel = "Notes";
        String transferLabel = "Transfer";

        float gapContentNote = gapNotesLabel + gapTransferLabel + measure.apply(notesLabel) - 2 * gap;

        // generate labels row

        stream.beginText();
        stream.newLineAtOffset(startX, curY);
        moveAfterText(stream, dateLabel, dateWordLength / 2 - measure.apply(dateLabel) / 2, 0, measure);

        moveAfterText(stream, separator, (dateWordLength - measure.apply(dateLabel)) / 2 + gap, 0,
                measure);

        moveAfterText(stream, notesLabel, gap + 50, 0, measure);

        moveAfterText(stream, separator, gap + 150, 0, measure);

        moveAfterText(stream, transferLabel, gap, 0, measure);

        stream.endText();

        curY -= (dy + 20);

        // generate separator line
        // magic :)

        stream.beginText();
        stream.newLineAtOffset(startX - 9, curY + 20);

        for (int i = 0; i < 57; ++i) {
            moveAfterText(stream, "-", 10, 0, measure);
        }
        stream.endText();

        TransactionService transactionService = new TransactionService();
        List<TransactionCheck> whereOriginAccountIdChecks = transactionService.readAllWhereOriginAccountId(acc.getId());
        List<TransactionCheck> whereTargetAccountIdChecks = transactionService.readAllWhereTargetAccountId(acc.getId());
        whereOriginAccountIdChecks.addAll(whereTargetAccountIdChecks);

        List<TransactionCheck> checks = whereOriginAccountIdChecks;

        Function<TransactionCheck, String> fMoney = check -> {
            double transfer = check.getTransferAmount();
            String strMoney = MoneyUtil.roundMoney(transfer) + "";
            System.out.println(strMoney);
            if (check.getOrigin().getId() == acc.getId()) {
                if (check.getDescription() == TransactionDescription.ACCOUNT_TRANSFER_SUB ||
                        check.getDescription() == TransactionDescription.ACCOUNT_ACCOUNT_TRANSFER) {
                    strMoney = "-" + strMoney;
                }
            }
            return strMoney;
        };
        Function<LocalDateTime, String> fDate = date -> date.format(ddMMYYYYFormat);
        Function<TransactionCheck, String> fNote = check -> {
            String note = "";
            TransactionDescription des = check.getDescription();
            if (TransactionDescription.ACCOUNT_TRANSFER_ADD == des) {
                note = "Money replanishment";
            } else if (TransactionDescription.ACCOUNT_TRANSFER_SUB == des) {
                note = "Money withdraw";
            } else if (TransactionDescription.ACCOUNT_ACCOUNT_TRANSFER == des) {
                Account origin = check.getOrigin();
                Account target = check.getTarget();
                note = "Money transfer";
                if (origin.getId().equals(acc.getId())) {
                    note += " to " + target.getUser().getFirstName();
                } else if (target.getId().equals(acc.getId())) {
                    note += " from " + origin.getUser().getFirstName();
                }
            }

            System.out.println(note);
            return note;
        };

        checks = checks.stream()
                .filter(check -> {
                    LocalDate date = check.getCreatedAt().toLocalDate();
                    return date.isAfter(from) && date.isBefore(to);
                })
                .sorted((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()))
                .toList();

        for (var check : checks) {
            curY = generateContentRow(
                    stream,
                    fDate.apply(check.getCreatedAt()),
                    fNote.apply(check),
                    fMoney.apply(check),
                    gap,
                    gapContentNote,
                    startX,
                    curY,
                    dy,
                    separator,
                    measure);
        }

        // curY = generateContentRow(
        // stream,
        // "1920.01.01",
        // "Ku",
        // "-1000.00",
        // gap,
        // gapContentNote,
        // startX,
        // curY,
        // dy,
        // "|",
        // measure);

        // generateContentRow(
        // stream,
        // "1920.01.01",
        // "Withdraw money",
        // "-1000.00",
        // gap,
        // gapContentNote,
        // startX,
        // curY,
        // dy,
        // "|",
        // measure);
    }

    private static float generateContentRow(
            PDPageContentStream stream,
            String date,
            String note,
            String transfer,
            float gap,
            float gapContentNote,
            final float startX,
            final float startY,
            float dy,
            String separator,
            Function<String, Float> measure) throws IOException {

        stream.beginText();
        moveAfterText(stream, date, startX, startY, measure);
        moveAfterText(stream, separator, gap, 0, measure);
        moveAfterText(stream, note, gap, 0, measure);
        stream.newLineAtOffset(gapContentNote - measure.apply(note), 0);
        moveAfterText(stream, separator, gap, 0, measure);
        moveAfterText(stream, transfer, gap, 0, measure);

        stream.endText();

        return startY - dy;
    }

    static void moveAfterText(PDPageContentStream stream, String text, float x, float y, Function<String, Float> length)
            throws IOException {
        stream.newLineAtOffset(x, y);
        stream.showText(text);
        stream.newLineAtOffset(length.apply(text), 0);
    }

    static <T> float generateRow(
            String label,
            T value,
            Function<T, String> transformer,
            PDPageContentStream content,
            final float startX,
            float startY,
            float width,
            String separator,
            float separatorGap,
            float dy,
            Function<String, Float> length) throws IOException {

        float center = width / 2;

        String apply = transformer.apply(value);

        content.beginText();
        content.newLineAtOffset(startX, startY);
        content.showText(label);

        content.newLineAtOffset(center - startX, 0);
        content.showText(separator);
        content.newLineAtOffset(separatorGap, 0);
        content.showText(apply);
        content.endText();

        return startY - dy;
    }

    static float generateHeader(
            String header,
            PDPageContentStream content,
            float x,
            float y,
            float dy) throws IOException {
        content.beginText();
        content.newLineAtOffset(x, y);
        content.showText(header);
        content.endText();

        return y - dy;
    }

}
