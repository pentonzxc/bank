package clevertec.transaction;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

import clevertec.Account;
import clevertec.transaction.check.TransactionCheck;
import clevertec.util.*;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Transaction {
    private String id;

    private Account main;

    private Account aux;

    public Transaction(@NonNull Account origin) {
        this(origin, null);
    }

    public Transaction(@NonNull Account main, Account aux) {
        this.main = main;
        this.aux = aux;

    }

    public List<TransactionCheck> beginTransaction(TransactionAction... actions) throws TransactionException {

        List<TransactionCheck> checks = null;

        try {
            if (aux == null) {
                synchronized (main.getLock()) {
                    checks = processTransaction(actions);
                }
            } else {
                Object lock1 = aux.getId() < main.getId() ? main.getLock() : aux.getLock();
                Object lock2 = aux.getId() < main.getId() ? aux.getLock() : main.getLock();
                synchronized (lock1) {
                    synchronized (lock2) {
                        checks = processTransaction(actions);
                    }
                }
            }
        } catch (TransactionException ex) {
            // FIXME handle:
            log.error("Transaction exception", ex);
            throw ex;
        }

        return checks;
        // handle transaction
    }

    // TODO maybe add rollback:
    private List<TransactionCheck> processTransaction(TransactionAction[] actions) throws TransactionException {
        Account copyMain = main.softCopy();
        Account copyAux = null;
        if (main == aux) {
            copyAux = copyMain;
        }

        else if (aux != null) {
            copyAux = aux.softCopy();
        }

        Pair<List<TransactionCheck>, Boolean> result = doTranscationActions(actions, copyMain, copyAux);

        List<TransactionCheck> checks = result.first();

        if (!result.second()) {
            String failMessage = "Can't process transaction: Id :: {} ; CheckId :: {}";
            log.debug(failMessage, this.id, checks.get(checks.size() - 1).getId());
            // FIXME add message:
            throw new TransactionException();
        }

        main.setMoney(copyMain.getMoney());
        if (copyAux != null)
            aux.setMoney(copyAux.getMoney());
        // log success

        return checks;
    }

    private Pair<List<TransactionCheck>, Boolean> doTranscationActions(
            TransactionAction[] actions,
            Account main,
            Account aux) {
        List<TransactionCheck> transactionChecks = new ArrayList<>();

        for (var action : actions) {
            ActionType actionType = action.getType();
            TransactionCheck transactionCheck = new TransactionCheck();
            ActionDirection actionDirection = ActionDirection.NONE;
            double change = action.getChange();
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

            transactionCheck.setDateTime(LocalDateTime.now(ZoneId.systemDefault()));
            transactionCheck.setTransferAmount(change);
            TransactionHelper.Check.resolveAndSetActionDescriptionInPlace(
                    transactionCheck,
                    actionType,
                    actionDirection,
                    main,
                    aux);
            TransactionHelper.Check.resolveAndSetOriginAndTargetInPlace(
                    transactionCheck,
                    transactionCheck.getDescription(),
                    actionType,
                    main,
                    aux);
            transactionChecks.add(transactionCheck);

            if (success == -1) {
                return new Pair<List<TransactionCheck>, Boolean>(transactionChecks, false);
            }

        }

        return new Pair<List<TransactionCheck>, Boolean>(transactionChecks, true);
    }

    public static Transaction between(Account main, Account aux) {
        return new Transaction(main, aux);
    }

    public Account getMain() {
        return main;
    }

    public Optional<Account> getAux() {
        return Optional.fromNullable(aux);
    }

}
