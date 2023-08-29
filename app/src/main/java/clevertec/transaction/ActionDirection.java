package clevertec.transaction;

/**
 * Represent transaction operation direction.
 * @see Transaction
 */
public enum ActionDirection {
    /**
     * Action to account.
     */
    ACCOUNT_TRANSFER,
    /**
     * Action from account to account.
     */
    ACCOUNT_ACCOUNT_TRANSFER,
    NONE
}
