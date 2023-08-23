package clevertec;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class TransactionAction {
    ActionType action;

    Integer change;

    public ActionType getType() {
        return action;
    }
}
