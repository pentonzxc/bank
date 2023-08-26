package clevertec.transaction.check;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.NoArgsConstructor;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getOriginBank() {
        return originBank;
    }

    public void setOriginBank(String originBank) {
        this.originBank = originBank;
    }

    public Optional<String> getTargetBank() {
        return targetBank;
    }

    public void setTargetBank(String targetBank) {
        this.targetBank = Optional.of(targetBank);
    }

    public String getOriginAccountNumber() {
        return originAccountNumber;
    }

    public void setOriginAccountNumber(String originAccountNumber) {
        this.originAccountNumber = originAccountNumber;
    }

    public Optional<String> getTargetAccountNumber() {
        return targetAccountNumber;
    }

    public void setTargetAccountNumber(String targetAccountNumber) {
        this.targetAccountNumber = Optional.of(targetAccountNumber);
    }

    public double getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(double money) {
        this.transferAmount = money;
    }

    public ActionDescription getDescription() {
        return description;
    }

    public void setDescription(ActionDescription description) {
        this.description = description;
    }

}
