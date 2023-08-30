package clevertec.transaction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import clevertec.Account;
import clevertec.transaction.check.ActionDescription;
import clevertec.transaction.check.TransactionCheck;
import clevertec.transaction.check.TransactionPrinter;
import clevertec.transaction.check.TransactionPrinterFactory;
import clevertec.util.DateUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper class.
 */
@Slf4j
public class TransactionHelper {
    private TransactionHelper() {
    }

    public class Check {

        public static final String CHECK_DIR_PATH = "check";

        private Check() {
        }

        /**
         * Resolve which type of description set.
         * <p>
         * If direction points only on one account, set that operation is between
         * one account, also it's depends on action type.
         * <p>
         * If direction points on two accounts, set that operation is between
         * two accounts.
         * 
         * @param check     - where to set
         * @param type      - based on that
         * @param direction - based on that
         * @param main      - based on that
         * @param aux       - based on that
         */

        static void resolveAndSetActionDescriptionInPlace(
                @NonNull TransactionCheck check,
                @NonNull ActionType type,
                @NonNull ActionDirection direction,
                @NonNull Account main,
                Account aux) {
            if ((direction == ActionDirection.ACCOUNT_TRANSFER || main.getId() == aux.getId())
                    && type == ActionType.ADD) {
                check.setDescription(ActionDescription.ACCOUNT_TRANSFER_ADD);
            } else if ((direction == ActionDirection.ACCOUNT_TRANSFER || main.getId() == aux.getId())
                    && type == ActionType.SUB) {
                check.setDescription(ActionDescription.ACCOUNT_TRANSFER_SUB);
            } else if (direction == ActionDirection.ACCOUNT_ACCOUNT_TRANSFER) {
                check.setDescription(ActionDescription.ACCOUNT_ACCOUNT_TRANSFER);
            } else {
                log.debug("TransactionHelper resolveMoneyDirectionInPlace receive illegal ActionDirection :: "
                        + direction);
            }
        }

        /**
         * Resolve where are origin, target banks/accounts and set them.
         * <p>
         * If description describe operation between one account, set origin and
         * target equals main.
         * <p>
         * If description describe operation between two accounts, if action type
         * add => origin = aux, target = main, else sub => origin = main, target = aux.
         * <p>
         * If any from accounts not contain a bank, throws {@link NullPointerException}.
         * {@link IllegalArgumentException}.
         * 
         * @param check       - where to set
         * @param description - based on it
         * @param type        - based on it
         * @param main        - contains main number and bank
         * @param aux         - contains aux number and bank
         */
        static void resolveAndSetOriginAndTargetInPlace(
                @NonNull TransactionCheck check,
                @NonNull ActionDescription description,
                @NonNull ActionType type,
                @NonNull Account main,
                Account aux) {
            if (description == ActionDescription.ACCOUNT_TRANSFER_ADD
                    || description == ActionDescription.ACCOUNT_TRANSFER_SUB) {
                check.setOrigin(main);
                check.setTarget(main);
            } else if (description == ActionDescription.ACCOUNT_ACCOUNT_TRANSFER && type == ActionType.ADD) {
                check.setOrigin(aux);
                check.setTarget(main);
            } else if (description == ActionDescription.ACCOUNT_ACCOUNT_TRANSFER && type == ActionType.SUB) {
                check.setOrigin(main);
                check.setTarget(aux);
            }
        }

        /**
         * Save check as file in {@link Check#CHECK_DIR_PATH}.
         * <p>
         * To transform check into string use {@link TransactionTransformer} type of
         * String.
         * 
         * @param check   - to save
         * @param printer - transformer
         * @return created file
         */
        public static File saveAsFile(@NonNull TransactionCheck check,
                @NonNull TransactionPrinter<String> printer) {
            String fileName = check.getId() + "|" + DateUtil.dateTimeToStringWithoutSeconds(check.getCreatedAt());
            try {
                return Files.writeString(
                        Path.of(CHECK_DIR_PATH, fileName),
                        printer.view(check),
                        StandardCharsets.UTF_8).toFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
