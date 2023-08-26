package clevertec.transaction.check;

import java.io.Serializable;

public interface TransactionPrinter<T extends Serializable> {
    public T view(TransactionCheck check);
}
