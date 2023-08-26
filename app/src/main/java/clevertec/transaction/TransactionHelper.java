package clevertec.transaction;

import clevertec.Account;
import clevertec.transaction.check.ActionDescription;
import clevertec.transaction.check.TransactionCheck;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class TransactionHelper {
    private TransactionHelper() {
    }

    class Check {
        private Check() {
        }
        public static void resolveAndSetActionDescriptionInPlace(
                TransactionCheck check,
                ActionType type,
                ActionDirection direction) {
            if (direction == ActionDirection.ACCOUNT_TRANSFER && type == ActionType.ADD) {
                check.setDescription(ActionDescription.ACCOUNT_TRANSFER_ADD);
            } else if (direction == ActionDirection.ACCOUNT_TRANSFER && type == ActionType.SUB) {
                check.setDescription(ActionDescription.ACCOUNT_TRANSFER_SUB);
            } else if (direction == ActionDirection.ACCOUNT_ACCOUNT_TRANSFER) {
                check.setDescription(ActionDescription.ACCOUNT_ACCOUNT_TRANSFER);
            } else {
                log.debug("TransactionHelper resolveMoneyDirectionInPlace receive illegal ActionDirection :: "
                        + direction);
            }
        }

        public static void resolveAndSetOriginAndTargetInPlace(
                TransactionCheck check,
                ActionDescription description,
                ActionType type,
                Account origin,
                Account target) {
            if (description == ActionDescription.ACCOUNT_TRANSFER_ADD
                    || description == ActionDescription.ACCOUNT_TRANSFER_SUB) {
                check.setOriginAccountNumber(origin.getAccountNumber());
                check.setOriginBank(origin.getBank().getName());
            } else if (description == ActionDescription.ACCOUNT_ACCOUNT_TRANSFER && type == ActionType.ADD) {
                check.setOriginAccountNumber(target.getAccountNumber());
                check.setOriginBank(target.getBank().getName());
                check.setTargetAccountNumber(origin.getAccountNumber());
                check.setTargetBank(origin.getBank().getName());
            } else if (description == ActionDescription.ACCOUNT_ACCOUNT_TRANSFER && type == ActionType.SUB) {
                check.setOriginAccountNumber(origin.getAccountNumber());
                check.setOriginBank(origin.getBank().getName());
                check.setTargetAccountNumber(target.getAccountNumber());
                check.setTargetBank(target.getBank().getName());
            }
        }
    }

}
