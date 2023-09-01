package clevertec.transaction.check;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import clevertec.account.Account;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Check for {@link Transaction}.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionCheck {
    UUID id;
    LocalDateTime createdAt;
    TransactionDescription description;
    Account origin;
    Account target;
    double transferAmount;
}
