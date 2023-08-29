package clevertec.transaction.check;

import java.io.Serializable;

/**
 * Interface that can transform check into view.
 */
public interface TransactionPrinter<T extends Serializable> {
    public T view(TransactionCheck check);
}