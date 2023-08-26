package clevertec.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class TransactionAction {
    ActionType action;

    double change;

    public ActionType getType() {
        return action;
    }


    static public TransactionAction from(ActionType action, double change){
        return new TransactionAction(action , change);
    }
}
    