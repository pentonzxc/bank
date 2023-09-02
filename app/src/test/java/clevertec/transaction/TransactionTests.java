package clevertec.transaction;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import clevertec.account.Account;
import clevertec.bank.Bank;
import clevertec.service.Instances;
import clevertec.transaction.check.TransactionCheck;
import clevertec.user.User;
import clevertec.util.DateUtil;

public class TransactionTests {
    List<Account> accounts = getAccounts();

    @Nested
    class Actual {
        @Test
        void whenTranscationOver_expectMoneyOnAccountChange() throws TransactionException {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            acc1.setBalance(100);
            acc2.setBalance(100);
            double unexpected1 = acc1.getBalance();
            double unexpected2 = acc2.getBalance();

            Transaction t = new Transaction(acc1, acc2);
            t.begin().transfer(TransactionAction.from(TransactionActionType.SUB, 100));

            assertAll(
                    () -> assertNotEquals(unexpected1, acc1.getBalance()),
                    () -> assertNotEquals(unexpected2, acc2.getBalance()));
        }

        @Test
        void whenActionTypeSub_expectLessMoneyOnOrigin() throws TransactionException {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            Account acc3 = accounts.get(2);

            acc1.setBalance(100);
            acc2.setBalance(100);
            acc3.setBalance(100);
            double before1 = acc1.getBalance();
            double before3 = acc3.getBalance();
            double expected1 = 0;
            double expected2 = 200;
            double expected3 = 90;

            Transaction t1 = new Transaction(acc1, acc2);
            Transaction t2 = new Transaction(acc3);
            TransactionComputation runner1 = t1.begin();
            TransactionComputation runner2 = t2.begin();
            runner1.transfer(TransactionAction.from(TransactionActionType.SUB, 100));
            runner2.transfer(TransactionAction.from(TransactionActionType.SUB, 10));

            assertAll(
                    () -> assertTrue(before1 > acc1.getBalance()),
                    () -> assertEquals(expected1, acc1.getBalance()),
                    () -> assertTrue(before3 > acc3.getBalance()),
                    () -> assertEquals(expected3, acc3.getBalance()),
                    () -> assertEquals(expected2, acc2.getBalance()));
        }

        @Test
        void whenActionTypeAdd_expectLessMoreOnOrigin() throws TransactionException {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            Account acc3 = accounts.get(2);

            acc1.setBalance(100);
            acc2.setBalance(100);
            acc3.setBalance(100);
            double before1 = acc1.getBalance();
            double before3 = acc3.getBalance();
            double expected1 = 110;
            double expected2 = 90;
            double expected3 = 110;

            Transaction t1 = new Transaction(acc1, acc2);
            Transaction t2 = new Transaction(acc3);
            TransactionComputation runner1 = t1.begin();
            TransactionComputation runner2 = t2.begin();
            runner1.transfer(TransactionAction.from(TransactionActionType.ADD, 10));
            runner2.transfer(TransactionAction.from(TransactionActionType.ADD, 10));

            assertAll(
                    () -> assertTrue(before1 < acc1.getBalance()),
                    () -> assertEquals(expected1, acc1.getBalance()),
                    () -> assertTrue(before3 < acc3.getBalance()),
                    () -> assertEquals(expected3, acc3.getBalance()),
                    () -> assertEquals(expected2, acc2.getBalance()));
        }

        @Test
        void whenActionAddNotEnoughMoneyToTransfer_expectTransactionException() throws TransactionException {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            acc1.setBalance(10);
            acc2.setBalance(0);

            double expected1 = acc1.getBalance();
            double expected2 = acc2.getBalance();

            Transaction t1 = new Transaction(acc1, acc2);
            TransactionComputation runner1 = t1.begin();
            Executable f = () -> runner1.transfer(TransactionAction.from(TransactionActionType.ADD, 10));
            assertAll(
                    () -> assertThrowsExactly(TransactionException.class, f),
                    () -> assertEquals(expected1, acc1.getBalance()),
                    () -> assertEquals(expected2, acc2.getBalance()));
        }

        @Test
        void whenActionSubAndNotEnoughMoneyToTransfer_expectTransactionException() throws TransactionException {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            acc1.setBalance(0);
            acc2.setBalance(10);

            double expected1 = acc1.getBalance();
            double expected2 = acc2.getBalance();

            Transaction t = new Transaction(acc1, acc2);
            TransactionComputation runner = t.begin();
            Executable f = () -> runner.transfer(TransactionAction.from(TransactionActionType.SUB, 10));

            assertAll(
                    () -> assertThrowsExactly(TransactionException.class, f),
                    () -> assertEquals(expected1, acc1.getBalance()),
                    () -> assertEquals(expected2, acc2.getBalance()));
        }

        @Test
        void whenWithdrawAndNotEnoughMoney_expectTransactionException() throws TransactionException {
            Account acc1 = accounts.get(0);
            acc1.setBalance(10);

            Transaction t = new Transaction(acc1);
            TransactionComputation runner = t.begin();
            Executable f = () -> runner.transfer(TransactionAction.from(TransactionActionType.SUB, 20));
            assertThrowsExactly(TransactionException.class, f);
        }

        @Test
        void whenTargetTheSameAsOriginAccount_expectMoneyChange() throws TransactionException {
            Account acc1 = accounts.get(0);
            acc1.setBalance(10);
            double before = acc1.getBalance();
            double expected1 = 1;
            double expected2 = 10;

            Transaction t1 = new Transaction(acc1, acc1);
            TransactionComputation runner1 = t1.begin();
            runner1.transfer(TransactionAction.from(TransactionActionType.SUB, 9));

            assertEquals(expected1, acc1.getBalance());

            Transaction t2 = new Transaction(acc1, acc1);
            TransactionComputation runner2 = t2.begin();

            runner2.transfer(TransactionAction.from(TransactionActionType.ADD, 9));

            assertEquals(expected2, acc1.getBalance());

        }

        @Test
        void whenTargetTheSameAsOriginAccountAndNotEnoughMoney_expectTransactionException()
                throws TransactionException {
            Account acc1 = accounts.get(0);
            acc1.setBalance(10);
            double before = acc1.getBalance();
            double expected = before;

            Transaction t = new Transaction(acc1, acc1);
            TransactionComputation runner = t.begin();
            Executable f = () -> runner.transfer(TransactionAction.from(TransactionActionType.SUB, 11));

            assertAll(
                    () -> assertThrowsExactly(TransactionException.class, f),
                    () -> assertEquals(before, expected));
        }

        // TODO: write parametrized test on two cases : origin and target, only origin
        @ParameterizedTest
        @ArgumentsSource(TransactionCheckProvider.class)
        void whenTransactionOver_expectProperTransactionCheck(TransactionCheck expected, Transaction transaction,
                TransactionAction transactionAction) throws TransactionException {
            TransactionCheck[] check_ = new TransactionCheck[1];
            LocalDateTime expectedDateTime = LocalDateTime.now(ZoneId.systemDefault());
            expected.setCreatedAt(expectedDateTime);

            try (MockedStatic<LocalDateTime> mockDateTime = Mockito.mockStatic(LocalDateTime.class)) {
                mockDateTime.when(() -> LocalDateTime.now(Mockito.any(ZoneId.class))).thenReturn(expectedDateTime);

                TransactionCheck check = transaction.begin().transfer(transactionAction);
                check_[0] = check;
                assertNotNull(check);
            }

            final TransactionCheck actual = check_[0];

            assertAll(
                    () -> assertEquals(expected.getOrigin().getAccountNumber(), actual.getOrigin().getAccountNumber()),
                    () -> assertEquals(expected.getTarget().getAccountNumber(),
                            actual.getTarget().getAccountNumber()),
                    () -> assertEquals(expected.getTransferAmount(), actual.getTransferAmount()),
                    () -> assertEquals(expected.getCreatedAt(), actual.getCreatedAt()),
                    () -> assertEquals(expected.getDescription(), actual.getDescription()));
        }

        @Test
        void whenGenerateCheck_expectedCheckInCheckDir() throws TransactionException, IOException {
            Account acc = new Account();
            LocalDateTime dateTime = LocalDateTime.now();

            acc.setId(1);
            acc.setBalance(100);
            acc.setBank(new Bank("ClevertecBank"));
            acc.setUser(new User("Nikolai", "Urusov", "2000-01-01"));
            acc.setAccountNumber("1234BYN");
            acc.setCurrency("BYN");
            acc.setOpeningDate(dateTime);

            try (MockedStatic<LocalDateTime> mockDateTime = Mockito.mockStatic(LocalDateTime.class)) {
                mockDateTime.when(() -> LocalDateTime.now(Mockito.any(ZoneId.class))).thenReturn(dateTime);

                Transaction transaction = new Transaction(acc);
                transaction.begin().transfer(new TransactionAction(TransactionActionType.ADD, 100), true);
            }

            String curPath = new File("").getCanonicalPath();
            File checkDir = new File(curPath.substring(0, curPath.length() - 4) + File.separator + "check");
            System.out.println(dateTime);

            assertTrue(Arrays.stream(checkDir.listFiles())
                    .map(File::getName)
                    .peek(f -> System.out.println(f))
                    .filter(name -> name.contains(DateUtil.dateTimeToStringWithoutSeconds(dateTime)))
                    .findFirst()
                    .isPresent());

        }
    }

    private List<Account> getAccounts() {
        return Instances.accountService().readAll();
    }
}
