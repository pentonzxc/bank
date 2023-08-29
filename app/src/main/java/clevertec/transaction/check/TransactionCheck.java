package clevertec.transaction.check;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import clevertec.Account;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Check for {@link Transaction}.
 */
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCheck {
    private UUID id;
    private LocalDateTime dateTime;
    private ActionDescription description;
    private Account origin;
    private Optional<Account> target = Optional.empty();
    private double transferAmount;

    // /**
    // *
    // * @param id - identification for check
    // * @param dateTime - time when transaction over
    // * @param description - {@link ActionDescription}
    // * @param originBank - bank name that made transaction
    // * @param originAccountNumber - origin bank account number
    // * @param targetBank - bank name to which the money was transferred
    // * @param targetAccountNumber - target bank account number
    // * @param transferAmount - amount transferred in the transaction
    // */
    // public TransactionCheck(
    // @NonNull UUID id,
    // @NonNull LocalDateTime dateTime,
    // @NonNull ActionDescription description,
    // @NonNull String originBank,
    // @NonNull String originAccountNumber,
    // @NonNull Optional<String> targetBank,
    // @NonNull Optional<String> targetAccountNumber,
    // double transferAmount) {
    // this.id = id;
    // this.dateTime = dateTime;
    // this.description = description;
    // this.originBank = originBank;
    // this.originAccountNumber = originAccountNumber;
    // this.targetBank = targetBank;
    // this.targetAccountNumber = targetAccountNumber;
    // this.transferAmount = transferAmount;
    // }

    /**
     * 
     * @return id
     */
    public UUID getId() {
        return id;
    }

    /**
     * 
     * @param id
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * 
     * @return LocalDateTime
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * 
     * @param dateTime
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return double
     */
    public double getTransferAmount() {
        return transferAmount;
    }

    public Account getOrigin() {
        return origin;
    }

    public void setOrigin(Account origin) {
        this.origin = origin;
    }

    public Optional<Account> getTarget() {
        return target;
    }

    public void setTarget(Account target) {
        this.target = Optional.ofNullable(target);
    }

    /**
     * @param money
     */
    public void setTransferAmount(double money) {
        this.transferAmount = money;
    }

    /**
     * @return ActionDescription
     */
    public ActionDescription getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(ActionDescription description) {
        this.description = description;
    }

}
