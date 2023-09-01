package clevertec.transaction;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import clevertec.account.Account;
import clevertec.transaction.check.TransactionCheck;
import clevertec.transaction.check.TransactionPrinter;
import clevertec.transaction.check.TransactionPrinterFactory;

/**
 * Class that is responsible for transaction computation process.
 */
public class TransactionComputation {

    private final Function<BiFunction<Account, Account, TransactionCheck>, TransactionCheck> transaction;

    private final Supplier<UUID> transactionId;
    private final Supplier<LocalDateTime> transactionFinishedAtDateTime;
    private final Consumer<Boolean> completeTransaction;

    volatile private boolean completed;

    TransactionComputation(
            Function<BiFunction<Account, Account, TransactionCheck>, TransactionCheck> transaction,
            Supplier<UUID> transactionId,
            Supplier<LocalDateTime> transactionFinishedAtDateTime,
            Consumer<Boolean> completeTransaction) {
        this.transaction = transaction;
        this.transactionId = transactionId;
        this.transactionFinishedAtDateTime = transactionFinishedAtDateTime;
        this.completeTransaction = completeTransaction;
        completed = false;
    }

    private Function<TransactionAction, BiFunction<Account, Account, TransactionCheck>> doTransfer = (
            TransactionAction action) -> (Account main, Account aux) -> {
                TransactionCheck check = new TransactionCheck();
                ActionType actionType = action.getType();
                ActionDirection actionDirection = ActionDirection.NONE;
                double change = action.getTransferAmount();
                double success = 0;

                if (main.getId() == aux.getId()) {
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

                check.setCreatedAt(LocalDateTime.now(ZoneId.systemDefault()));
                check.setTransferAmount(change);
                TransactionHelper.Check.resolveAndSetActionDescriptionInPlace(
                        check,
                        actionType,
                        actionDirection,
                        main,
                        aux);
                TransactionHelper.Check.resolveAndSetOriginAndTargetInPlace(
                        check,
                        check.getDescription(),
                        actionType,
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
            if (completed) {
                throw new TransactionRuntimeException("Can't run twice the same transaction");
            }
            TransactionCheck check = transaction.apply(doTransfer.apply(action));
            check.setId(transactionId.get());
            check.setCreatedAt(transactionFinishedAtDateTime.get());
            if (saveCheck) {
                TransactionHelper.Check.saveAsFile(check, TransactionPrinterFactory.stringPrinter());
            }
            return check;
        } catch (TransactionRuntimeException ex) {
            throw new TransactionException(ex);
        } finally {
            completeTransaction.accept(true);
            completed = true;
        }
    }

    public TransactionCheck transfer(TransactionAction action) throws TransactionException {
        return transfer(action, false);
    }

    public boolean isCompleted() {
        return completed;
    }

}
