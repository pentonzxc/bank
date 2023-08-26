package clevertec.transaction;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import clevertec.Account;
import clevertec.transaction.check.TransactionCheck;
import clevertec.util.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Transaction {
    String id;

    Account origin;

    Account target;

    public Transaction(Account origin) {
        this(origin, null);
    }

    public Transaction(Account origin, Account target) {
        this.origin = origin;
        this.target = target;

    }

    public List<TransactionCheck> beginTransaction(TransactionAction... actions) {

        List<TransactionCheck> checks = null;

        try {
            if (target == null) {
                synchronized (origin.getLock()) {
                    processTransaction(actions);
                }
            } else {
                Object lock1 = target.getId() < origin.getId() ? origin.getLock() : target.getLock();
                Object lock2 = target.getId() < origin.getId() ? target.getLock() : origin.getLock();
                synchronized (lock1) {
                    synchronized (lock2) {
                        checks = processTransaction(actions);
                    }
                }
            }
        } catch (TransactionException ex) {
            // FIXME handle:
            log.error("Transaction exception", ex);
        }

        return checks;
        // handle transaction
    }

    // TODO maybe add rollback:
    private List<TransactionCheck> processTransaction(TransactionAction[] actions) throws TransactionException {
        Account copyOrigin = new Account(origin.getMoney());
        Account copyTarget = target != null ? new Account(target.getMoney()) : null;

        Pair<List<TransactionCheck>, Boolean> result = doTranscationActions(actions, copyOrigin, copyTarget);

        List<TransactionCheck> checks = result.first();

        if (!result.second()) {
            String failMessage = "Can't process transaction: Id :: {} ; CheckId :: {}";
            log.debug(failMessage, this.id, checks.get(checks.size() - 1).getId());
            // FIXME add message:
            throw new TransactionException();
        }

        origin.setMoney(copyOrigin.getMoney());
        if (copyTarget != null)
            target.setMoney(copyTarget.getMoney());
        // log success

        return checks;
    }

    private Pair<List<TransactionCheck>, Boolean> doTranscationActions(
            TransactionAction[] actions,
            Account origin,
            Account target) {
        List<TransactionCheck> transactionChecks = new ArrayList<>();

        for (var action : actions) {
            ActionType actionType = action.getType();
            TransactionCheck transactionCheck = new TransactionCheck();
            ActionDirection actionDirection = ActionDirection.NONE;
            double change = action.getChange();
            double success = 0;

            if (target == null) {
                if (actionType == ActionType.ADD) {
                    origin.addMoney(change);
                } else {
                    success = origin.subMoney(change);
                }

                actionDirection = ActionDirection.ACCOUNT_TRANSFER;
            } else {
                // TODO: some misunderstanding
                if (actionType == ActionType.ADD) {
                    success = target.transfer(origin, change);
                } else {
                    success = origin.transfer(target, change);
                }

                actionDirection = ActionDirection.ACCOUNT_ACCOUNT_TRANSFER;
            }

            transactionCheck.setDateTime(LocalDateTime.now(ZoneId.systemDefault()));
            transactionCheck.setMoney(change);
            TransactionHelper.Check.resolveAndSetActionDescriptionInPlace(transactionCheck, actionType,
                    actionDirection);
            TransactionHelper.Check.resolveAndSetOriginAndTargetInPlace(
                    transactionCheck,
                    transactionCheck.getDescription(),
                    actionType,
                    origin,
                    target);
            transactionChecks.add(transactionCheck);

            if (success == -1) {
                return new Pair<List<TransactionCheck>, Boolean>(transactionChecks, false);
            }

        }

        return new Pair<List<TransactionCheck>, Boolean>(transactionChecks, true);
    }
}
