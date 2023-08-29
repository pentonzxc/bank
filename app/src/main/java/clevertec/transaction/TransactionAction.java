package clevertec.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * Class holder of transaction change.
 */
@Data
@AllArgsConstructor
public class TransactionAction {
    ActionType action;

    double transferAmount;

    /**
     * @return ActionType
     */
    public ActionType getType() {
        return action;
    }

    /**
     * More appropriate form to create TransactionAction.
     * 
     * @param action         - what type of transaction dp
     * @param transferAmount - on how many balance is changed
     * @return TransactionAction
     */
    static public TransactionAction from(@NonNull ActionType action, double transferAmount) {
        return new TransactionAction(action, transferAmount);
    }
}
