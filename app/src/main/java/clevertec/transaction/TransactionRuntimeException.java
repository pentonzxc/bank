package clevertec.transaction;

/**
 * Exception that is threw in Transactions classes.
 */
class TransactionRuntimeException extends RuntimeException {

    public TransactionRuntimeException() {
        super();
    }

    public TransactionRuntimeException(String msg) {
        super(msg);
    }
}
