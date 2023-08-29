package clevertec.transaction;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import clevertec.Account;
import clevertec.transaction.check.TransactionCheck;
import clevertec.transaction.check.TransactionPrinter;
import clevertec.transaction.check.TransactionPrinterFactory;

/**
 * Class that is responsible for transaction computation process.
 */
public class TransactionComputation {

    private String id;

    private final Function<BiFunction<Account, Account, TransactionCheck>, TransactionCheck> transaction;

    private final Supplier<String> transactionId;

    TransactionComputation(
            Function<BiFunction<Account, Account, TransactionCheck>, TransactionCheck> transaction,
            Supplier<String> transactionId) {
        this.transaction = transaction;
        this.transactionId = transactionId;
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
    public TransactionCheck transfer(TransactionAction action, boolean saveCheck) throws TransactionException {
        try {
            if (id != null) {
                throw new TransactionRuntimeException("Can't run twice the same transaction");
            }
            TransactionCheck check = transaction.apply(doTransfer.apply(action));
            this.setId(transactionId.get());
            if (saveCheck) {
                TransactionHelper.Check.saveAsFile(check, TransactionPrinterFactory.stringPrinter());
            }
            return check;
        } catch (TransactionRuntimeException ex) {
            throw new TransactionException(ex);
        }
    }

    public TransactionCheck transfer(TransactionAction action) throws TransactionException {
        return transfer(action, false);
    }

    public void setId(String id) {
        this.id = id;
    }

}
