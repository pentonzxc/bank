package clevertec.transaction;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import clevertec.Account;
import clevertec.FindAllService;
import clevertec.transaction.check.ActionDescription;
import clevertec.transaction.check.TransactionCheck;
import clevertec.util.Pair;

public class TransactionTests {
    List<Account> accounts = getAccounts();

    @Nested
    class Actual {
        @Test
        void whenTranscationOver_expectMoneyOnAccountChange() throws TransactionException {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            acc1.setMoney(100);
            acc2.setMoney(100);
            double unexpected1 = acc1.getMoney();
            double unexpected2 = acc2.getMoney();

            Transaction t = new Transaction(acc1, acc2);
            t.begin().run(TransactionAction.from(ActionType.SUB, 100));

            assertAll(
                    () -> assertNotEquals(unexpected1, acc1.getMoney()),
                    () -> assertNotEquals(unexpected2, acc2.getMoney()));
        }

        @Test
        void whenActionTypeSub_expectLessMoneyOnOrigin() throws TransactionException {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            Account acc3 = accounts.get(2);

            acc1.setMoney(100);
            acc2.setMoney(100);
            acc3.setMoney(100);
            double before1 = acc1.getMoney();
            double before3 = acc3.getMoney();
            double expected1 = 0;
            double expected2 = 200;
            double expected3 = 90;

            Transaction t1 = new Transaction(acc1, acc2);
            Transaction t2 = new Transaction(acc3);
            TransactionRunner runner1 = t1.begin();
            TransactionRunner runner2 = t2.begin();
            runner1.run(TransactionAction.from(ActionType.SUB, 100));
            runner2.run(TransactionAction.from(ActionType.SUB, 10));

            assertAll(
                    () -> assertTrue(before1 > acc1.getMoney()),
                    () -> assertEquals(expected1, acc1.getMoney()),
                    () -> assertTrue(before3 > acc3.getMoney()),
                    () -> assertEquals(expected3, acc3.getMoney()),
                    () -> assertEquals(expected2, acc2.getMoney()));
        }

        @Test
        void whenActionTypeAdd_expectLessMoreOnOrigin() throws TransactionException {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            Account acc3 = accounts.get(2);

            acc1.setMoney(100);
            acc2.setMoney(100);
            acc3.setMoney(100);
            double before1 = acc1.getMoney();
            double before2 = acc2.getMoney();
            double before3 = acc3.getMoney();
            double expected1 = 110;
            double expected2 = 90;
            double expected3 = 110;

            Transaction t1 = new Transaction(acc1, acc2);
            Transaction t2 = new Transaction(acc3);
            TransactionRunner runner1 = t1.begin();
            TransactionRunner runner2 = t2.begin();
            runner1.run(TransactionAction.from(ActionType.ADD, 10));
            runner2.run(TransactionAction.from(ActionType.ADD, 10));

            assertAll(
                    () -> assertTrue(before1 < acc1.getMoney()),
                    () -> assertEquals(expected1, acc1.getMoney()),
                    () -> assertTrue(before3 < acc3.getMoney()),
                    () -> assertEquals(expected3, acc3.getMoney()),
                    () -> assertEquals(expected2, acc2.getMoney()));
        }

        @Test
        void whenActionAddNotEnoughMoneyToTransfer_expectTransactionException() {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            acc1.setMoney(10);
            acc2.setMoney(0);

            double expected1 = acc1.getMoney();
            double expected2 = acc2.getMoney();

            Transaction t1 = new Transaction(acc1, acc2);
            TransactionRunner runner1 = t1.begin();
            Executable f = () -> runner1.run(TransactionAction.from(ActionType.ADD, 10));
            assertAll(
                    () -> assertThrowsExactly(TransactionException.class, f),
                    () -> assertEquals(expected1, acc1.getMoney()),
                    () -> assertEquals(expected2, acc2.getMoney()));
        }

        @Test
        void whenActionSubAndNotEnoughMoneyToTransfer_expectTransactionException() {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            acc1.setMoney(0);
            acc2.setMoney(10);

            double expected1 = acc1.getMoney();
            double expected2 = acc2.getMoney();

            Transaction t = new Transaction(acc1, acc2);
            TransactionRunner runner = t.begin();
            Executable f = () -> runner.run(TransactionAction.from(ActionType.SUB, 10));

            assertAll(
                    () -> assertThrowsExactly(TransactionException.class, f),
                    () -> assertEquals(expected1, acc1.getMoney()),
                    () -> assertEquals(expected2, acc2.getMoney()));
        }

        @Test
        void whenWithdrawAndNotEnoughMoney_expectTransactionException() {
            Account acc1 = accounts.get(0);
            acc1.setMoney(10);

            Transaction t = new Transaction(acc1);
            TransactionRunner runner = t.begin();
            Executable f = () -> runner.run(TransactionAction.from(ActionType.SUB, 20));
            assertThrowsExactly(TransactionException.class, f);
        }

        @Test
        void whenTargetTheSameAsOriginAccount_expectMoneyChangeEqualZero() throws TransactionException {
            Account acc1 = accounts.get(0);
            acc1.setMoney(10);
            double before = acc1.getMoney();
            double expected = before;

            Transaction t = new Transaction(acc1, acc1);
            TransactionRunner runner = t.begin();
            runner.run(TransactionAction.from(ActionType.SUB, 9));

            assertEquals(expected, acc1.getMoney());

            runner.run(TransactionAction.from(ActionType.ADD, 9));

            assertEquals(expected, acc1.getMoney());
        }

        @Test
        void whenTargetTheSameAsOriginAccountAndNotEnoughMoney_expectTransactionException() {
            Account acc1 = accounts.get(0);
            acc1.setMoney(10);
            double before = acc1.getMoney();
            double expected = before;

            Transaction t = new Transaction(acc1, acc1);
            TransactionRunner runner = t.begin();
            Executable f = () -> runner.run(TransactionAction.from(ActionType.SUB, 11));

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
            expected.setDateTime(expectedDateTime);

            try (MockedStatic<LocalDateTime> mockDateTime = Mockito.mockStatic(LocalDateTime.class)) {
                mockDateTime.when(() -> LocalDateTime.now(Mockito.any(ZoneId.class))).thenReturn(expectedDateTime);

                TransactionCheck check = transaction.begin().run(transactionAction);
                check_[0] = check;
                assertNotNull(check);
            }

            final TransactionCheck check = check_[0];

            assertAll(
                    () -> assertEquals(expected.getOriginBank(), check.getOriginBank()),
                    () -> assertEquals(expected.getTargetBank().get(), check.getTargetBank().get()),
                    () -> assertEquals(expected.getOriginAccountNumber(), check.getOriginAccountNumber()),
                    () -> assertEquals(expected.getTargetAccountNumber().get(), check.getTargetAccountNumber().get()),
                    () -> assertEquals(expected.getTransferAmount(), check.getTransferAmount()),
                    () -> assertEquals(expected.getDateTime(), check.getDateTime()),
                    () -> assertEquals(expected.getDescription(), check.getDescription()));
        }
    }

    @Nested
    class Deprecated {

        @Test
        void whenTranscationOver_expectMoneyOnAccountChange() throws TransactionException {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            acc1.setMoney(100);
            acc2.setMoney(100);
            double unexpected1 = acc1.getMoney();
            double unexpected2 = acc2.getMoney();

            Transaction t = new Transaction(acc1, acc2);
            t.beginTransaction(
                    TransactionAction.from(ActionType.SUB, 100));

            assertAll(
                    () -> assertNotEquals(unexpected1, acc1.getMoney()),
                    () -> assertNotEquals(unexpected2, acc2.getMoney()));
        }

        @Test
        void whenActionTypeSub_expectLessMoneyOnOrigin() throws TransactionException {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            Account acc3 = accounts.get(2);

            acc1.setMoney(100);
            acc2.setMoney(100);
            acc3.setMoney(100);
            double before1 = acc1.getMoney();
            double before3 = acc3.getMoney();
            double expected1 = 0;
            double expected2 = 200;
            double expected3 = 90;

            Transaction t1 = new Transaction(acc1, acc2);
            Transaction t2 = new Transaction(acc3);
            t1.beginTransaction(
                    TransactionAction.from(ActionType.SUB, 100));
            t2.beginTransaction(TransactionAction.from(ActionType.SUB, 10));

            assertAll(
                    () -> assertTrue(before1 > acc1.getMoney()),
                    () -> assertEquals(expected1, acc1.getMoney()),
                    () -> assertTrue(before3 > acc3.getMoney()),
                    () -> assertEquals(expected3, acc3.getMoney()),
                    () -> assertEquals(expected2, acc2.getMoney()));
        }

        @Test
        void whenActionTypeAdd_expectLessMoreOnOrigin() throws TransactionException {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            Account acc3 = accounts.get(2);

            acc1.setMoney(100);
            acc2.setMoney(100);
            acc3.setMoney(100);
            double before1 = acc1.getMoney();
            double before2 = acc2.getMoney();
            double before3 = acc3.getMoney();
            double expected1 = 110;
            double expected2 = 90;
            double expected3 = 110;

            Transaction t1 = new Transaction(acc1, acc2);
            Transaction t2 = new Transaction(acc3);
            t1.beginTransaction(
                    TransactionAction.from(ActionType.ADD, 10));
            t2.beginTransaction(TransactionAction.from(ActionType.ADD, 10));

            assertAll(
                    () -> assertTrue(before1 < acc1.getMoney()),
                    () -> assertEquals(expected1, acc1.getMoney()),
                    () -> assertTrue(before3 < acc3.getMoney()),
                    () -> assertEquals(expected3, acc3.getMoney()),
                    () -> assertEquals(expected2, acc2.getMoney()));
        }

        @Test
        void whenActionAddNotEnoughMoneyToTransfer_expectTransactionException() {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            acc1.setMoney(10);
            acc2.setMoney(0);

            double expected1 = acc1.getMoney();
            double expected2 = acc2.getMoney();

            Transaction t1 = new Transaction(acc1, acc2);
            Executable f = () -> t1.beginTransaction(TransactionAction.from(ActionType.ADD, 10));
            assertAll(
                    () -> assertThrowsExactly(TransactionException.class, f),
                    () -> assertEquals(expected1, acc1.getMoney()),
                    () -> assertEquals(expected2, acc2.getMoney()));
        }

        @Test
        void whenActionSubAndNotEnoughMoneyToTransfer_expectTransactionException() {
            Account acc1 = accounts.get(0);
            Account acc2 = accounts.get(1);
            acc1.setMoney(0);
            acc2.setMoney(10);

            double expected1 = acc1.getMoney();
            double expected2 = acc2.getMoney();

            Transaction t = new Transaction(acc1, acc2);
            Executable f = () -> t.beginTransaction(TransactionAction.from(ActionType.SUB, 10));

            assertAll(
                    () -> assertThrowsExactly(TransactionException.class, f),
                    () -> assertEquals(expected1, acc1.getMoney()),
                    () -> assertEquals(expected2, acc2.getMoney()));
        }

        @Test
        void whenWithdrawAndNotEnoughMoney_expectTransactionException() {
            Account acc1 = accounts.get(0);
            acc1.setMoney(10);

            Transaction t = new Transaction(acc1);
            Executable f = () -> t.beginTransaction(TransactionAction.from(ActionType.SUB, 20));
            assertThrowsExactly(TransactionException.class, f);
        }

        @Test
        void whenTargetTheSameAsOriginAccount_expectMoneyChangeEqualZero() throws TransactionException {
            Account acc1 = accounts.get(0);
            acc1.setMoney(10);
            double before = acc1.getMoney();
            double expected = before;

            Transaction t = new Transaction(acc1, acc1);
            t.beginTransaction(TransactionAction.from(ActionType.SUB, 9));

            assertEquals(expected, acc1.getMoney());

            t.beginTransaction(TransactionAction.from(ActionType.ADD, 9));

            assertEquals(expected, acc1.getMoney());
        }

        @Test
        void whenTargetTheSameAsOriginAccountAndNotEnoughMoney_expectTransactionException() {
            Account acc1 = accounts.get(0);
            acc1.setMoney(10);
            double before = acc1.getMoney();
            double expected = before;

            Transaction t = new Transaction(acc1, acc1);
            Executable f = () -> t.beginTransaction(TransactionAction.from(ActionType.SUB, 11));

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
            expected.setDateTime(expectedDateTime);

            try (MockedStatic<LocalDateTime> mockDateTime = Mockito.mockStatic(LocalDateTime.class)) {
                mockDateTime.when(() -> LocalDateTime.now(Mockito.any(ZoneId.class))).thenReturn(expectedDateTime);

                TransactionCheck check = transaction.beginTransaction(transactionAction);
                check_[0] = check;
                assertNotNull(check);
            }

            final TransactionCheck check = check_[0];

            assertAll(
                    () -> assertEquals(expected.getOriginBank(), check.getOriginBank()),
                    () -> assertEquals(expected.getTargetBank().get(), check.getTargetBank().get()),
                    () -> assertEquals(expected.getOriginAccountNumber(), check.getOriginAccountNumber()),
                    () -> assertEquals(expected.getTargetAccountNumber().get(), check.getTargetAccountNumber().get()),
                    () -> assertEquals(expected.getTransferAmount(), check.getTransferAmount()),
                    () -> assertEquals(expected.getDateTime(), check.getDateTime()),
                    () -> assertEquals(expected.getDescription(), check.getDescription()));

        }
    }

    private List<Account> getAccounts() {
        return FindAllService.accounts();
    }
}
