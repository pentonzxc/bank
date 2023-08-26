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

    Account initiator;

    Account target;

    public Transaction(Account initiator) {
        this(initiator, null);
    }

    public Transaction(Account initiator, Account target) {
        this.initiator = initiator;
        this.target = target;

    }

    public List<TransactionCheck> beginTransaction(TransactionAction... actions) {

        List<TransactionCheck> checks = null;

        try {
            if (target == null) {
                synchronized (initiator.getLock()) {
                    processTransaction(actions);
                }
            } else {
                Object lock1 = target.getId() < initiator.getId() ? initiator.getLock() : target.getLock();
                Object lock2 = target.getId() < initiator.getId() ? target.getLock() : initiator.getLock();
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
        Account copyInitiator = new Account(initiator.getMoney());
        Account copyTarget = target != null ? new Account(target.getMoney()) : null;

        Pair<List<TransactionCheck>, Boolean> result = doTranscationActions(actions, copyInitiator, copyTarget);

        List<TransactionCheck> checks = result.first();

        if (!result.second()) {
            String failMessage = "Can't process transaction: Id :: {} ; CheckId :: {}";
            log.debug(failMessage, this.id, checks.get(checks.size() - 1).getId());
            // FIXME add message:
            throw new TransactionException();
        }

        initiator.setMoney(copyInitiator.getMoney());
        if (copyTarget != null)
            target.setMoney(copyTarget.getMoney());
        // log success

        return checks;
    }

    private Pair<List<TransactionCheck>, Boolean> doTranscationActions(
            TransactionAction[] actions,
            Account initiator,
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
                    initiator.addMoney(change);
                } else {
                    success = initiator.subMoney(change);
                }

                actionDirection = ActionDirection.ACCOUNT_TRANSFER;
            } else {
                // TODO: some misunderstanding
                if (actionType == ActionType.ADD) {
                    success = target.transfer(initiator, change);
                } else {
                    success = initiator.transfer(target, change);
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
                    initiator,
                    target);
            transactionChecks.add(transactionCheck);

            if (success == -1) {
                return new Pair<List<TransactionCheck>, Boolean>(transactionChecks, false);
            }

        }

        return new Pair<List<TransactionCheck>, Boolean>(transactionChecks, true);
    }
}
