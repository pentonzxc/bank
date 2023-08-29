package clevertec.transaction;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.base.Optional;

import clevertec.Account;
import clevertec.transaction.check.TransactionCheck;
import clevertec.util.Pair;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Transaction is a class that represent change on account or accounts.
 *
 */
// @Data
@Slf4j
public class Transaction {
    private String id;

    private Account main;

    private Account aux;

    private LocalDateTime beginDateTime;

    private LocalDateTime endDateTime;

    public Transaction(@NonNull Account origin) {
        this(origin, null);
    }

    /**
     * Transaction will be over main and aux.
     * 
     * @param main - the most significant side of the transaction.
     * @param aux  - the second side of the transaction.
     */
    public Transaction(@NonNull Account main, Account aux) {
        this.main = main;
        this.aux = aux;
    }

    private Function<BiFunction<Account, Account, TransactionCheck>, TransactionCheck> onFailureRollback = f -> {
        Account copyMain = main.softCopy();
        Account copyAux = null;
        TransactionCheck check = null;

        if (main == aux) {
            copyAux = copyMain;
        } else if (aux != null) {
            copyAux = aux.softCopy();
        }

        check = f.apply(copyMain, copyAux);

        main.setBalance(copyMain.getBalance());
        if (copyAux != null) {
            aux.setBalance(copyAux.getBalance());
        }

        return check;
    };

    private Function<BiFunction<Account, Account, TransactionCheck>, TransactionCheck> transaction = f -> {
        TransactionCheck check = null;
        beginDateTime = LocalDateTime.now();
        if (aux == null) {
            synchronized (main.getLock()) {
                check = onFailureRollback.apply(f);
            }
        } else {
            Object lock1 = aux.getId() < main.getId() ? main.getLock() : aux.getLock();
            Object lock2 = aux.getId() < main.getId() ? aux.getLock() : main.getLock();
            synchronized (lock1) {
                synchronized (lock2) {
                    check = onFailureRollback.apply(f);
                }
            }
        }

        // id = generateTransactionId()
        endDateTime = LocalDateTime.now();
        return check;
    };

    /**
     * Some rules:
     * <p>
     * 1) Transaction <b>cannot run twice and more</b>, else throws
     * {@link TransactionException}.
     * <p>
     * 2) The direction of the operations <b>depends on how many participants</b> in
     * the
     * transaction.
     * <b>Only one participant</b>,
     * operations like <b>add replenish
     * account balance</b> and like <b>sub withdraw money</b> from the
     * account.
     * Two participant <b>depends on in which order you pass your accounts
     * arguments, when
     * create Transaction</b>.
     * First order - the most significant. It's called main argument.
     * Operations on it equal operations on only one participant, but money flow
     * the next:
     * <b>add => main <- aux, sub => main -> aux </b>
     * <p>
     * 3) If money doesn't enough to perform any operation on any account,
     * <b>Transaction will be rollbacked</b> and throwed
     * {@link TransactionException}
     * 
     * 
     * @see TransactionException
     * @see Transaction#Transaction(Account)
     * @see Transaction#Transaction(Account, Account)
     * @see Transaction#between(Account, Account)
     * 
     * @return TransactionComputation
     */
    public TransactionComputation begin() throws TransactionException {
        if (id != null) {
            throw new TransactionException("Can't run twice the same transaction");
        }
        return new TransactionComputation(transaction, () -> id);
    }

    /**
     * @param action
     * @return TransactionCheck
     * @throws TransactionException
     */
    @Deprecated(since = "feature/refactor_transaction")
    public TransactionCheck beginTransaction(TransactionAction action) throws TransactionException {

        TransactionCheck check = null;

        try {
            if (aux == null) {
                synchronized (main.getLock()) {
                    check = processTransaction(action);
                }
            } else {
                Object lock1 = aux.getId() < main.getId() ? main.getLock() : aux.getLock();
                Object lock2 = aux.getId() < main.getId() ? aux.getLock() : main.getLock();
                synchronized (lock1) {
                    synchronized (lock2) {
                        check = processTransaction(action);
                    }
                }
            }
        } catch (TransactionException ex) {
            // FIXME handle:
            log.error("Transaction exception", ex);
            throw ex;
        }

        return check;
        // handle transaction
    }

    /**
     * @param action
     * @return TransactionCheck
     * @throws TransactionException
     */
    @Deprecated(since = "feature/refactor_transaction")
    // TODO maybe add rollback:
    private TransactionCheck processTransaction(TransactionAction action) throws TransactionException {
        Account copyMain = main.softCopy();
        Account copyAux = null;
        if (main == aux) {
            copyAux = copyMain;
        }

        else if (aux != null) {
            copyAux = aux.softCopy();
        }

        Pair<TransactionCheck, Boolean> result = doTranscationActions(action, copyMain, copyAux);

        TransactionCheck check = result.first();

        if (!result.second()) {
            // String failMessage = "Can't process transaction: Id :: {} ; CheckId :: {}";
            // log.debug(failMessage, this.id, check.getId());
            // FIXME add message:
            throw new TransactionException();
        }

        main.setBalance(copyMain.getBalance());
        if (copyAux != null)
            aux.setBalance(copyAux.getBalance());
        // log success

        return check;
    }

    /**
     * @param action
     * @param main
     * @param aux
     * @return Pair<TransactionCheck, Boolean>
     */
    @Deprecated(since = "feature/refactor_transaction")
    private Pair<TransactionCheck, Boolean> doTranscationActions(
            TransactionAction action,
            Account main,
            Account aux) {
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
            return new Pair<TransactionCheck, Boolean>(check, false);
        }

        return new Pair<TransactionCheck, Boolean>(check, true);
    }

    /**
     * 
     * More appropriate api to create transaction.
     * 
     * @see Transaction#Transaction(Account, Account)
     * @param main - main account
     * @param aux  - aux account
     * @return Transaction
     */
    public static Transaction between(Account main, Account aux) {
        return new Transaction(main, aux);
    }

    void setId(String id) {
        this.id = id;
    }

    /**
     * @return Account
     */
    public Account getMain() {
        return main;
    }

    /**
     * @return Optional<Account>
     */
    public Optional<Account> getAux() {
        return Optional.fromNullable(aux);
    }
}
