package clevertec;

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
}
