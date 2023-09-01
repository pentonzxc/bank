package clevertec.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.Value;

/**
 * Class holder of transaction change.
 */
@Value
public class TransactionAction {
    TransactionActionType action;

    double transferAmount;

    /**
     * More appropriate form to create TransactionAction.
     * 
     * @param action         - what type of transaction dp
     * @param transferAmount - on how many balance is changed
     * @return TransactionAction
     */
    static public TransactionAction from(@NonNull TransactionActionType action, double transferAmount) {
        return new TransactionAction(action, transferAmount);
    }
}
