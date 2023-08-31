package clevertec.transaction.check;

import java.util.Arrays;
import java.util.Optional;

/**
 * Describe the transaction <b>action that occurred</b>.
 * 
 * @see clevertec.transaction.Transaction
 */
public enum TransactionDescription {
    /**
     * Account replenishment.
     */
    ACCOUNT_TRANSFER_ADD("Пополнение"),
    /**
     * Withdraw from account.
     */
    ACCOUNT_TRANSFER_SUB("Вывод"),
    /**
     * Account to account money transfer.
     */
    ACCOUNT_ACCOUNT_TRANSFER("Перевод на счёт");

    private final String description;

    private TransactionDescription(String description) {
        this.description = description;
    }

    static public Optional<TransactionDescription> fromDescription(String description) {
        return Arrays.stream(TransactionDescription.values())
                .filter(t -> description.equals(t.description()))
                .findFirst();
    }

    /**
     * 
     * @return description.
     */
    public String description() {
        return description;
    }

}
