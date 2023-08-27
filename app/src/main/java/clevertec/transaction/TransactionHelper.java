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
                ActionDirection direction,
                Account main,
                Account aux) {
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

        public static void resolveAndSetOriginAndTargetInPlace(
                TransactionCheck check,
                ActionDescription description,
                ActionType type,
                Account main,
                Account aux) {
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
