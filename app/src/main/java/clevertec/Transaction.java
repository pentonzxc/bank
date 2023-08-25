package clevertec;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

    public void beginTransaction(TransactionAction... actions) {
        if (target == null) {
            synchronized (initiator.LOCK) {
                processTransaction(actions);
            }
        } else {
            Object lock1 = target.id < initiator.id ? initiator.LOCK : target.LOCK;
            Object lock2 = target.id < initiator.id ? target.LOCK : initiator.LOCK;
            synchronized (lock1) {
                synchronized (lock2) {
                    processTransaction(actions);
                }
            }
        }
        // handle transaction
    }

    // TODO: maybe add rollback....
    private boolean processTransaction(TransactionAction[] actions) {
        for (var action : actions) {
            double change = action.getChange();
            ActionType actionType = action.getType();
            double success = 0;

            if (target == null) {
                if (actionType == ActionType.ADD) {
                    initiator.addMoney(change);
                } else {
                    success = initiator.subMoney(change);
                }
            } else {
                // TODO: some misunderstanding
                if (actionType == ActionType.ADD) {
                    success = target.transfer(initiator, change);
                } else {
                    success = initiator.transfer(target, change);
                }
            }

            // TODO: think about immediately cancelation
            if (success == -1) {
                String failMessage = """
                        Can't process transaction: Id :: {} ; AccountId :: {} ;
                        Reason: Don't enough money , expected >= {} , actual = {}
                            """;
                Account guilty = actionType == actionType.ADD ? target : initiator;
                log.debug(failMessage, this.id, guilty.getId(), change, guilty.getMoney());
                return false;
            }
            // log success
        }
        return true;
    }

    // private boolean compareStrings(String s1, String s2) {
    // if (s1 == null)
    // return false;
    // else if (s2 == null)
    // return false;
    // return s1.compareTo(s2) >= 1;
    // }

}
