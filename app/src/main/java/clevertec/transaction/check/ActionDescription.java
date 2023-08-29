package clevertec.transaction.check;

/**
 * Describe the transaction <b>action that occurred</b>.
 * 
 * @see clevertec.transaction.Transaction
 */
public enum ActionDescription {
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

    private ActionDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return description.
     */
    public String getDescription() {
        return description;
    }

}
