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
                argument1());
    }

    private Arguments argument1() {
        TransactionCheck check = new TransactionCheck();
        check.setOriginAccountNumber("TESTN1");
        check.setTargetAccountNumber("TESTN2");
        check.setTargetBank("TEST2");
        check.setOriginBank("TEST1");
        check.setTransferAmount(100);
        check.setDescription(ActionDescription.ACCOUNT_ACCOUNT_TRANSFER);

        Account acc1 = new Account();
        Account acc2 = new Account();
        acc1.setBank(new Bank("TEST2"));
        acc1.setId(1);
        acc2.setBank(new Bank("TEST1"));
        acc1.setMoney(150);
        acc2.setMoney(150);
        acc2.setId(2);
        acc1.setAccountNumber("TESTN2");
        acc2.setAccountNumber("TESTN1");

        Transaction transaction = new Transaction(acc1, acc2);
        TransactionAction transactionAction = TransactionAction.from(ActionType.ADD, 100);

        return Arguments.of(check, transaction, transactionAction);
    }

}
