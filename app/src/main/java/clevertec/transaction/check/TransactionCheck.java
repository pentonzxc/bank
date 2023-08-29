package clevertec.transaction.check;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Check for {@link Transaction}.
 */
@NoArgsConstructor
public class TransactionCheck {
    private String id;
    private LocalDateTime dateTime;
    private ActionDescription description;
    private String originBank;
    private String originAccountNumber;
    private Optional<String> targetBank = Optional.empty();
    private Optional<String> targetAccountNumber = Optional.empty();
    private double transferAmount;

    /**
     * 
     * @param id                  - identification for check
     * @param dateTime            - time when transaction over
     * @param description         - {@link ActionDescription}
     * @param originBank          - bank name that made transaction
     * @param originAccountNumber - origin bank account number
     * @param targetBank          - bank name to which the money was transferred
     * @param targetAccountNumber - target bank account number
     * @param transferAmount      - amount transferred in the transaction
     */
    public TransactionCheck(
            @NonNull String id,
            @NonNull LocalDateTime dateTime,
            @NonNull ActionDescription description,
            @NonNull String originBank,
            @NonNull String originAccountNumber,
            @NonNull Optional<String> targetBank,
            @NonNull Optional<String> targetAccountNumber,
            double transferAmount) {
        this.id = id;
        this.dateTime = dateTime;
        this.description = description;
        this.originBank = originBank;
        this.originAccountNumber = originAccountNumber;
        this.targetBank = targetBank;
        this.targetAccountNumber = targetAccountNumber;
        this.transferAmount = transferAmount;
    }

    /**
     * 
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     */
    public void setId(String id) {
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
     * @return
     */
    public String getOriginBank() {
        return originBank;
    }

    /**
     * @param originBank
     */
    public void setOriginBank(String originBank) {
        this.originBank = originBank;
    }

    /**
     * @return Optional<String>
     */
    public Optional<String> getTargetBank() {
        return targetBank;
    }

    /**
     * @param targetBank
     */
    public void setTargetBank(String targetBank) {
        this.targetBank = Optional.of(targetBank);
    }

    /**
     * @return String
     */
    public String getOriginAccountNumber() {
        return originAccountNumber;
    }

    /**
     * @param originAccountNumber
     */
    public void setOriginAccountNumber(String originAccountNumber) {
        this.originAccountNumber = originAccountNumber;
    }

    /**
     * @return Optional<String>
     */
    public Optional<String> getTargetAccountNumber() {
        return targetAccountNumber;
    }

    /**
     * @param targetAccountNumber
     */
    public void setTargetAccountNumber(String targetAccountNumber) {
        this.targetAccountNumber = Optional.of(targetAccountNumber);
    }

    /**
     * @return double
     */
    public double getTransferAmount() {
        return transferAmount;
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
