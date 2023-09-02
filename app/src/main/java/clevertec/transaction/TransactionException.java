package clevertec.transaction;

/**
 * Exception that is threw in Transactions classes.
 */
public class TransactionException extends Exception {

    public TransactionException() {
        super();
    }

    public TransactionException(Throwable var1) {
        super(var1);
    }

    public TransactionException(String msg) {
        super(msg);
    }
}
