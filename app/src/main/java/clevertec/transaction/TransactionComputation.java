package clevertec.transaction;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.BiFunction;
import java.util.function.Function;

import clevertec.Account;
import clevertec.transaction.check.TransactionCheck;

/**
 * Class that is responsible for transaction computation process.
 */
public class TransactionComputation {

    private final Function<BiFunction<Account, Account, TransactionCheck>, TransactionCheck> transaction;

    TransactionComputation(
            Function<BiFunction<Account, Account, TransactionCheck>, TransactionCheck> transaction) {
        this.transaction = transaction;
    }

    private Function<TransactionAction, BiFunction<Account, Account, TransactionCheck>> doTransfer = (
            TransactionAction action) -> (Account main, Account aux) -> {
                TransactionCheck check = new TransactionCheck();
                ActionType actionType = action.getType();
                ActionDirection actionDirection = ActionDirection.NONE;
                double change = action.getTransferAmount();
                double success = 0;

                if (aux == null) {
                    if (actionType == ActionType.ADD) {
                        main.addMoney(change);
                    } else {
                        success = main.subMoney(change);
                    }

                    actionDirection = ActionDirection.ACCOUNT_TRANSFER;
                } else {
                    // TODO: some misunderstanding
                    if (actionType == ActionType.ADD) {
                        success = aux.transfer(main, change);
                    } else {
                        success = main.transfer(aux, change);
                    }

                    actionDirection = ActionDirection.ACCOUNT_ACCOUNT_TRANSFER;
                }

                check.setDateTime(LocalDateTime.now(ZoneId.systemDefault()));
                check.setTransferAmount(change);
                TransactionHelper.Check.resolveAndSetActionDescriptionInPlace(check, actionType, actionDirection, main,
                        aux);
                TransactionHelper.Check.resolveAndSetOriginAndTargetInPlace(check, check.getDescription(), actionType,
                        main,
                        aux);

                if (success == -1) {
                    throw new TransactionRuntimeException();
                }

                return check;
            };

    /**
     * Compute transaction
     * 
     * @param action - action to transfer
     * @return TransactionCheck
     * @throws TransactionException
     */
    public TransactionCheck transfer(TransactionAction action) throws TransactionException {

        try {
            return transaction.apply(doTransfer.apply(action));
        } catch (TransactionRuntimeException ex) {
            throw new TransactionException(ex);
        }
    }

}
