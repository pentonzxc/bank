package clevertec.transaction.check;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import clevertec.account.Account;
import clevertec.transaction.Transaction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    @JsonProperty("created_at")
    LocalDateTime createdAt;
    TransactionDescription description;
    Account origin;
    Account target;
    @JsonProperty("transfer_amount")
    double transferAmount;
}
