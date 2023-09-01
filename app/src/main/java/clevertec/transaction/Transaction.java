package clevertec.transaction;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import clevertec.account.Account;
import clevertec.transaction.check.TransactionCheck;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

/**
 * 
 * Transaction is a class that represent change on account or accounts.
 *
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {
    @Getter(AccessLevel.NONE)
    UUID id;

    Account main;

    Account aux;

    LocalDateTime finishedAt;

    @Getter(AccessLevel.NONE)
    volatile boolean[] completed;

    /**
     * Transaction for one account.
     * 
     * @param origin
     */
    public Transaction(@NonNull Account origin) {
        this(origin, origin);
    }

    /**
     * Transaction will be over main and aux.
     * 
     * @param main - the main side of the transaction.
     * @param aux  - the aux side of the transaction.
     */
    public Transaction(@NonNull Account main, @NonNull Account aux) {
        this.main = main;
        this.aux = aux;
        this.completed = new boolean[1];
    }

    Function<BiFunction<Account, Account, TransactionCheck>, TransactionCheck> onFailureRollback = f -> {
        Account copyMain = main.softCopy();
        Account copyAux = main.getId() != aux.getId() ? aux.softCopy() : copyMain;
        TransactionCheck check = null;

        check = f.apply(copyMain, copyAux);

        main.setBalance(copyMain.getBalance());
        aux.setBalance(copyAux.getBalance());

        return check;
    };

    Function<BiFunction<Account, Account, TransactionCheck>, TransactionCheck> transaction = f -> {
        TransactionCheck check = null;

        Object lock1 = aux.getId() < main.getId() ? main.getLock() : aux.getLock();
        Object lock2 = aux.getId() < main.getId() ? aux.getLock() : main.getLock();
        synchronized (lock1) {
            synchronized (lock2) {
                check = onFailureRollback.apply(f);
            }
        }
        finishedAt = LocalDateTime.now(ZoneId.systemDefault());
        id = UUID.randomUUID();

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
     * <b>Transaction will be rollbacked</b> and thrown
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
        return new TransactionComputation(transaction, () -> id, () -> finishedAt,
                (complete) -> completed[0] = complete);
    }

    /**
     * 
     * More appropriate api to create transaction over two accounts.
     * 
     * @see Transaction#Transaction(Account, Account)
     * @param main - main account
     * @param aux  - aux account
     * @return Transaction
     */
    public static Transaction between(@NonNull Account main, @NonNull Account aux) {
        return new Transaction(main, aux);
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
    public Account getAux() {
        return aux;
    }

    public Optional<UUID> getId() {
        return Optional.ofNullable(id);
    }

    public boolean isCompleted() {
        return completed[0];
    }
}
