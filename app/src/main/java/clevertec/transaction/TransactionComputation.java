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
import clevertec.transaction.check.TransactionPrinterFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Class that is responsible for transaction computation process.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionComputation {

    final Function<BiFunction<Account, Account, TransactionCheck>, TransactionCheck> transaction;
    final Supplier<UUID> transactionId;
    final Supplier<LocalDateTime> transactionFinishedAtDateTime;
    final Consumer<Boolean> completeTransaction;
    @Getter
    volatile boolean completed;

    final Function<TransactionAction, BiFunction<Account, Account, TransactionCheck>> doTransfer = (
            TransactionAction action) -> (Account main, Account aux) -> {
                TransactionCheck check = new TransactionCheck();
                TransactionActionDirection actionDirection = TransactionActionDirection.NONE;
                double success = 0;
                TransactionActionType actionType = action.getAction();
                double change = action.getTransferAmount();

                if (main.getId() == aux.getId()) {
                    if (actionType == TransactionActionType.ADD) {
                        main.addMoney(change);
                    } else {
                        success = main.subMoney(change);
                    }
                    actionDirection = TransactionActionDirection.ACCOUNT_TRANSFER;
                } else {
                    if (actionType == TransactionActionType.ADD) {
                        success = aux.transfer(main, change);
                    } else {
                        success = main.transfer(aux, change);
                    }
                    actionDirection = TransactionActionDirection.ACCOUNT_ACCOUNT_TRANSFER;
                }

                if (success == -1) {
                    throw new TransactionRuntimeException();
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

                return check;
            };

    /**
     * Compute transaction and return transaction check of this transaction.
     * <p>
     * If saveCheck, save check as file in check folder.
     * <p>
     * If run twice, throws exception.
     * 
     * @see TransactionHelper.Check#CHECK_DIR_PATH
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

    /**
     * Compute transaction and return transaction check of this transaction.
     * 
     * @see TransactionComputation#transfer(TransactionAction, boolean)
     * @see TransactionHelper.Check#CHECK_DIR_PATH
     * @param action - action to transfer
     * @return TransactionCheck
     * @throws TransactionException
     */
    public TransactionCheck transfer(TransactionAction action) throws TransactionException {
        return transfer(action, false);
    }
}
