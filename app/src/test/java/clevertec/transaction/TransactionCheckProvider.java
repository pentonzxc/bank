package clevertec.transaction;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import clevertec.account.Account;
import clevertec.bank.Bank;
import clevertec.transaction.check.TransactionDescription;
import clevertec.transaction.check.TransactionCheck;

public class TransactionCheckProvider implements ArgumentsProvider {

    /**
     * @param context
     * @return Stream<? extends Arguments>
     * @throws Exception
     */
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {

        return Stream.of(
                argument1(),
                argument2(),
                argument3(),
                argument4(),
                argument5(),
                argument6());
    }

    private Arguments argument1() {
        TransactionCheck check = new TransactionCheck();
        double change = 100;

        Account acc1 = new Account();
        Account acc2 = new Account();
        acc1.setBank(new Bank("TEST1"));
        acc1.setId(1);
        acc2.setBank(new Bank("TEST2"));
        acc1.setBalance(150);
        acc2.setBalance(150);
        acc2.setId(2);
        acc1.setAccountNumber("TEST1N");
        acc2.setAccountNumber("TEST2N");

        check.setOrigin(acc2);
        check.setTarget(acc1);
        check.setTransferAmount(change);
        check.setDescription(TransactionDescription.ACCOUNT_ACCOUNT_TRANSFER);

        Transaction transaction = new Transaction(acc1, acc2);
        TransactionAction transactionAction = TransactionAction.from(TransactionActionType.ADD, change);

        return Arguments.of(check, transaction, transactionAction);
    }

    private Arguments argument2() {
        TransactionCheck check = new TransactionCheck();

        Account acc1 = new Account();
        Account acc2 = new Account();
        double change = 100;

        acc1.setBank(new Bank("TEST1"));
        acc1.setId(1);
        acc2.setBank(new Bank("TEST2"));
        acc1.setBalance(150);
        acc2.setBalance(150);
        acc2.setId(2);
        acc1.setAccountNumber("TEST1N");
        acc2.setAccountNumber("TEST2N");

        check.setOrigin(acc1);
        check.setTarget(acc2);
        check.setTransferAmount(change);
        check.setDescription(TransactionDescription.ACCOUNT_ACCOUNT_TRANSFER);

        Transaction transaction = new Transaction(acc1, acc2);
        TransactionAction transactionAction = TransactionAction.from(TransactionActionType.SUB, change);

        return Arguments.of(check, transaction, transactionAction);
    }

    private Arguments argument3() {
        TransactionCheck check = new TransactionCheck();

        Account acc1 = new Account();
        double change = 100;

        acc1.setBank(new Bank("TEST1"));
        acc1.setId(1);
        acc1.setBalance(150);
        acc1.setAccountNumber("TEST1N");

        check.setOrigin(acc1);
        check.setTarget(acc1);
        check.setTransferAmount(change);
        check.setDescription(TransactionDescription.ACCOUNT_TRANSFER_ADD);

        Transaction transaction = new Transaction(acc1, acc1);
        TransactionAction transactionAction = TransactionAction.from(TransactionActionType.ADD, change);

        return Arguments.of(check, transaction, transactionAction);
    }

    private Arguments argument4() {
        TransactionCheck check = new TransactionCheck();

        Account acc1 = new Account();
        double change = 100;

        acc1.setBank(new Bank("TEST1"));
        acc1.setId(1);
        acc1.setBalance(150);
        acc1.setAccountNumber("TEST1N");

        check.setOrigin(acc1);
        check.setTarget(acc1);
        check.setTransferAmount(change);
        check.setDescription(TransactionDescription.ACCOUNT_TRANSFER_SUB);

        Transaction transaction = new Transaction(acc1, acc1);
        TransactionAction transactionAction = TransactionAction.from(TransactionActionType.SUB, change);

        return Arguments.of(check, transaction, transactionAction);
    }

    private Arguments argument5() {
        TransactionCheck check = new TransactionCheck();

        Account acc1 = new Account();
        double change = 100;

        acc1.setBank(new Bank("TEST1"));
        acc1.setId(1);
        acc1.setBalance(150);
        acc1.setAccountNumber("TEST1N");

        check.setOrigin(acc1);
        check.setTarget(acc1);
        check.setTransferAmount(change);
        check.setDescription(TransactionDescription.ACCOUNT_TRANSFER_ADD);

        Transaction transaction = new Transaction(acc1);
        TransactionAction transactionAction = TransactionAction.from(TransactionActionType.ADD, change);

        return Arguments.of(check, transaction, transactionAction);
    }

    private Arguments argument6() {
        TransactionCheck check = new TransactionCheck();

        Account acc1 = new Account();
        double change = 100;

        acc1.setBank(new Bank("TEST1"));
        acc1.setId(1);
        acc1.setBalance(150);
        acc1.setAccountNumber("TEST1N");

        check.setOrigin(acc1);
        check.setTarget(acc1);
        check.setTransferAmount(change);
        check.setDescription(TransactionDescription.ACCOUNT_TRANSFER_SUB);

        Transaction transaction = new Transaction(acc1);
        TransactionAction transactionAction = TransactionAction.from(TransactionActionType.SUB, change);

        return Arguments.of(check, transaction, transactionAction);
    }

}
