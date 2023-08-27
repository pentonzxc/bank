package clevertec.transaction;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import clevertec.Account;
import clevertec.Bank;
import clevertec.transaction.check.ActionDescription;
import clevertec.transaction.check.TransactionCheck;
import clevertec.util.Pair;
import net.bytebuddy.asm.Advice.Argument;

public class TransactionCheckProvider implements ArgumentsProvider {

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
        acc1.setMoney(150);
        acc2.setMoney(150);
        acc2.setId(2);
        acc1.setAccountNumber("TEST1N");
        acc2.setAccountNumber("TEST2N");

        check.setOriginBank(acc2.getBank().getName());
        check.setOriginAccountNumber(acc2.getAccountNumber());
        check.setTargetBank(acc1.getBank().getName());
        check.setTargetAccountNumber(acc1.getAccountNumber());
        check.setTransferAmount(change);
        check.setDescription(ActionDescription.ACCOUNT_ACCOUNT_TRANSFER);

        Transaction transaction = new Transaction(acc1, acc2);
        TransactionAction transactionAction = TransactionAction.from(ActionType.ADD, change);

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
        acc1.setMoney(150);
        acc2.setMoney(150);
        acc2.setId(2);
        acc1.setAccountNumber("TEST1N");
        acc2.setAccountNumber("TEST2N");

        check.setOriginBank(acc1.getBank().getName());
        check.setOriginAccountNumber(acc1.getAccountNumber());
        check.setTargetBank(acc2.getBank().getName());
        check.setTargetAccountNumber(acc2.getAccountNumber());
        check.setTransferAmount(change);
        check.setDescription(ActionDescription.ACCOUNT_ACCOUNT_TRANSFER);

        Transaction transaction = new Transaction(acc1, acc2);
        TransactionAction transactionAction = TransactionAction.from(ActionType.SUB, change);

        return Arguments.of(check, transaction, transactionAction);
    }

    private Arguments argument3() {
        TransactionCheck check = new TransactionCheck();

        Account acc1 = new Account();
        Account acc2 = acc1;
        double change = 100;

        acc1.setBank(new Bank("TEST1"));
        acc1.setId(1);
        acc1.setMoney(150);
        acc1.setAccountNumber("TEST1N");

        check.setOriginBank(acc1.getBank().getName());
        check.setOriginAccountNumber(acc1.getAccountNumber());
        check.setTargetBank(acc1.getBank().getName());
        check.setTargetAccountNumber(acc1.getAccountNumber());
        check.setTransferAmount(change);
        check.setDescription(ActionDescription.ACCOUNT_TRANSFER_ADD);

        Transaction transaction = new Transaction(acc1, acc2);
        TransactionAction transactionAction = TransactionAction.from(ActionType.ADD, change);

        return Arguments.of(check, transaction, transactionAction);
    }

    private Arguments argument4() {
        TransactionCheck check = new TransactionCheck();

        Account acc1 = new Account();
        Account acc2 = acc1;
        double change = 100;

        acc1.setBank(new Bank("TEST1"));
        acc1.setId(1);
        acc1.setMoney(150);
        acc1.setAccountNumber("TEST1N");

        check.setOriginBank(acc1.getBank().getName());
        check.setOriginAccountNumber(acc1.getAccountNumber());
        check.setTargetBank(acc1.getBank().getName());
        check.setTargetAccountNumber(acc1.getAccountNumber());
        check.setTransferAmount(change);
        check.setDescription(ActionDescription.ACCOUNT_TRANSFER_SUB);

        Transaction transaction = new Transaction(acc1, acc2);
        TransactionAction transactionAction = TransactionAction.from(ActionType.SUB, change);

        return Arguments.of(check, transaction, transactionAction);
    }

    private Arguments argument5() {
        TransactionCheck check = new TransactionCheck();

        Account acc1 = new Account();
        double change = 100;

        acc1.setBank(new Bank("TEST1"));
        acc1.setId(1);
        acc1.setMoney(150);
        acc1.setAccountNumber("TEST1N");

        check.setOriginBank(acc1.getBank().getName());
        check.setOriginAccountNumber(acc1.getAccountNumber());
        check.setTargetBank(acc1.getBank().getName());
        check.setTargetAccountNumber(acc1.getAccountNumber());
        check.setTransferAmount(change);
        check.setDescription(ActionDescription.ACCOUNT_TRANSFER_ADD);

        Transaction transaction = new Transaction(acc1);
        TransactionAction transactionAction = TransactionAction.from(ActionType.ADD, change);

        return Arguments.of(check, transaction, transactionAction);
    }

    private Arguments argument6() {
        TransactionCheck check = new TransactionCheck();

        Account acc1 = new Account();
        double change = 100;

        acc1.setBank(new Bank("TEST1"));
        acc1.setId(1);
        acc1.setMoney(150);
        acc1.setAccountNumber("TEST1N");

        check.setOriginBank(acc1.getBank().getName());
        check.setOriginAccountNumber(acc1.getAccountNumber());
        check.setTargetBank(acc1.getBank().getName());
        check.setTargetAccountNumber(acc1.getAccountNumber());
        check.setTransferAmount(change);
        check.setDescription(ActionDescription.ACCOUNT_TRANSFER_SUB);

        Transaction transaction = new Transaction(acc1);
        TransactionAction transactionAction = TransactionAction.from(ActionType.SUB, change);

        return Arguments.of(check, transaction, transactionAction);
    }

}
