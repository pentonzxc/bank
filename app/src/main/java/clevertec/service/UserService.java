package clevertec.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.function.Function;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import clevertec.Account;
import clevertec.Bank;
import clevertec.User;

public class UserService {

    // public File generateBankAccountsStatement(Account ) {

    // return null;
    // }

    public static File generateBankAccountStatement(Account acc) throws IOException {
        File file = new File("test.pdf");
        // if (!file.exists())
        // file.createNewFile();
        PDDocument pdf = new PDDocument();
        pdf.addPage(new PDPage());

        PDPage page = pdf.getPage(0);

        PDFont font = PDType1Font.HELVETICA_BOLD;
        int fontSize = 12;

        PDPageContentStream contentStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND,
                true, true);

        contentStream.setFont(font, fontSize);

        // float width = page.getMediaBox().getWidth();
        // float diffX = (width - 1);
        // float diffY = 10;

        // final float startX = page.getMediaBox().getLowerLeftX();
        // float curY = page.getMediaBox().getUpperRightY() - 50;

        // String header = "Extraction";

        // contentStream.beginText();
        // contentStream.newLineAtOffset((width / 2) - header.length() / 2, curY);
        // contentStream.showText(header);
        // contentStream.endText();

        // curY -= 20;

        float startX = 50;
        float curY = page.getMediaBox().getUpperRightY() - 50;
        float dy = 20;
        String separator = "|";
        float separatorGap = 10;
        float width = page.getMediaBox().getWidth();

        Function<String, Float> lengthOfWord = str -> {
            try {
                return font.getStringWidth(str) / 1000 * fontSize;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        // float letterWidth = font.getStringWidth(text.substring(start, i)) / 1000 *
        // fontSize;

        String bankLabel = "Bank";
        String clientLabel = "Client";
        String currencyLabel = "Currency";
        String accountOpeningDateLabel = "Opening date";
        String periodOfBankStatement = "Period";
        String createdAtLabel = "Date and time of creation";
        String balanceLabel = "Balance";

        String header = "Bank statement";
        curY = generateHeader(
                bankLabel,
                contentStream,
                (width / 2) - lengthOfWord.apply(header.substring(0, header.length() / 2)),
                curY,
                dy);

        Function<Account, Bank> g1 = Account::getBank;
        Function<Bank, String> f1 = Bank::getName;

        curY = generateRow(
                bankLabel,
                acc,
                f1.compose(g1),
                contentStream,
                startX,
                curY,
                width,
                separator,
                separatorGap,
                dy,
                lengthOfWord);

        Function<Account, User> g2 = Account::getUser;
        Function<User, String> f2 = user -> user.getFirstName() + user.getLastName();

        curY = generateRow(
                clientLabel,
                acc,
                f2.compose(g2),
                contentStream,
                startX,
                curY,
                width,
                separator,
                separatorGap,
                dy,
                lengthOfWord);

        curY = generateRow(
                createdAtLabel,
                LocalDate.now().toString(),
                Function.identity(),
                contentStream,
                startX,
                curY,
                width,
                separator,
                separatorGap,
                dy,
                lengthOfWord);
        // contentStream.beginText();
        // contentStream.newLineAtOffset(0, curY);
        // contentStream.showText(labelBank);
        // contentStream.newLineAtOffset((width / 2) - header.length() / 2 -
        // labelBank.length() + 10, 0);
        // contentStream.showText("|");
        // contentStream.newLineAtOffset(20, 0);
        // contentStream.showText(valueBank);
        // contentStream.endText();

        contentStream.close(); // don't forget that one!

        pdf.save(file);

        return null;
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
