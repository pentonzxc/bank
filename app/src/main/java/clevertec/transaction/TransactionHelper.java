package clevertec.transaction;

import clevertec.Account;
import clevertec.transaction.check.ActionDescription;
import clevertec.transaction.check.TransactionCheck;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper class.
 */
@Slf4j
class TransactionHelper {
    private TransactionHelper() {
    }

    class Check {
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

        public static void resolveAndSetActionDescriptionInPlace(
                @NonNull TransactionCheck check,
                @NonNull ActionType type,
                @NonNull ActionDirection direction,
                @NonNull Account main,
                @NonNull Account aux) {
            if ((direction == ActionDirection.ACCOUNT_TRANSFER || main == aux) && type == ActionType.ADD) {
                check.setDescription(ActionDescription.ACCOUNT_TRANSFER_ADD);
            } else if ((direction == ActionDirection.ACCOUNT_TRANSFER || main == aux) && type == ActionType.SUB) {
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
        public static void resolveAndSetOriginAndTargetInPlace(
                @NonNull TransactionCheck check,
                @NonNull ActionDescription description,
                @NonNull ActionType type,
                @NonNull Account main,
                @NonNull Account aux) {
            if (description == ActionDescription.ACCOUNT_TRANSFER_ADD
                    || description == ActionDescription.ACCOUNT_TRANSFER_SUB) {
                check.setOriginAccountNumber(main.getAccountNumber());
                check.setOriginBank(main.getBank().getName());
                check.setTargetAccountNumber(main.getAccountNumber());
                check.setTargetBank(main.getBank().getName());
            } else if (description == ActionDescription.ACCOUNT_ACCOUNT_TRANSFER && type == ActionType.ADD) {
                check.setOriginAccountNumber(aux.getAccountNumber());
                check.setOriginBank(aux.getBank().getName());
                check.setTargetAccountNumber(main.getAccountNumber());
                check.setTargetBank(main.getBank().getName());
            } else if (description == ActionDescription.ACCOUNT_ACCOUNT_TRANSFER && type == ActionType.SUB) {
                check.setOriginAccountNumber(main.getAccountNumber());
                check.setOriginBank(main.getBank().getName());
                check.setTargetAccountNumber(aux.getAccountNumber());
                check.setTargetBank(aux.getBank().getName());
            }
        }
    }

}
