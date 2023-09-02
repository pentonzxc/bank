package clevertec.account;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import clevertec.bank.Bank;
import clevertec.service.GenerateAccountBankStatement;
import clevertec.service.TransactionService;
import clevertec.transaction.check.TransactionCheck;
import clevertec.transaction.check.TransactionDescription;
import clevertec.user.User;

@ExtendWith(MockitoExtension.class)
public class PdfBankStatementTest {

        GenerateAccountBankStatement generateAccountBankStatement;

        @Test
        void whenGeneratePdf_expectedPdf() throws IOException {
                TransactionCheck check1 = new TransactionCheck();
                TransactionCheck check2 = new TransactionCheck();
                TransactionCheck check3 = new TransactionCheck();
                TransactionCheck check4 = new TransactionCheck();
                Account acc = new Account();
                Account acc2 = new Account();
                Account acc3 = new Account();
                acc2.setId(2);
                acc3.setId(3);

                acc.setId(1);
                acc.setAccountNumber("123BYN");
                acc.setBalance(100);
                acc.setOpeningDate(LocalDateTime.now().minusYears(3));
                acc.setCurrency("BYN");
                acc.setUser(new User("Nikolai", "Urusov", "2003-01-01"));
                acc.setBank(new Bank("ClevertecBank"));

                acc2.setUser(new User("Peter", "Balk", "2001-12-12"));
                acc3.setUser(new User("Kur", "Sam", "1921-12-12"));

                check1.setOrigin(acc);
                check1.setTarget(acc2);
                check1.setTransferAmount(100);
                check1.setDescription(TransactionDescription.ACCOUNT_ACCOUNT_TRANSFER);
                check1.setCreatedAt(LocalDateTime.now().minusYears(5));

                check2.setOrigin(acc);
                check2.setTarget(acc);
                check2.setCreatedAt(LocalDateTime.now().minusYears(3));
                check2.setTransferAmount(50);
                check2.setDescription(TransactionDescription.ACCOUNT_TRANSFER_SUB);

                check3.setOrigin(acc);
                check3.setTarget(acc);
                check3.setCreatedAt(LocalDateTime.now().minusYears(2));
                check3.setTransferAmount(150);
                check3.setDescription(TransactionDescription.ACCOUNT_TRANSFER_ADD);

                check4.setOrigin(acc3);
                check4.setTarget(acc);
                check4.setCreatedAt(LocalDateTime.now().minusYears(1));
                check4.setTransferAmount(200);
                check4.setDescription(TransactionDescription.ACCOUNT_ACCOUNT_TRANSFER);

                List<TransactionCheck> whereOrigin = new ArrayList<>();
                whereOrigin.addAll(List.of(
                                check1,
                                check2,
                                check3));

                List<TransactionCheck> whereTarget = List.of(
                                check2,
                                check3,
                                check4);

                TransactionService transactionService = Mockito.mock(TransactionService.class);

                Mockito.when(transactionService.readAllWhereOriginAccountId(acc.getId())).thenReturn(
                                whereOrigin);

                Mockito.when(transactionService.readAllWhereTargetAccountId(acc.getId())).thenReturn(
                                whereTarget);

                generateAccountBankStatement = new GenerateAccountBankStatement(transactionService);

                generateAccountBankStatement.generateBankAccountStatement(
                                acc,
                                LocalDate.now().minusYears(10),
                                LocalDate.now());

                // expected following
                // |
                /*
                 * Bank statement
                 * Bank| ClevertecBank
                 * Client| Nikolai Urusov
                 * Account| 123BYN
                 * Currency| BYN
                 * Opening date| 01.09.2020
                 * Period| 01.09.2013 - 01.09.2023
                 * Date and time of creation| 01.09.2023, 22.36
                 * Balance| 100.0 BYN
                 * Date
                 * |
                 * Notes
                 * |
                 * Transfer
                 * ---------------------------------------------------------
                 * 01.09.2018|Money transfer to Peter|-100.0
                 * 01.09.2020|Money withdraw|-50.0
                 * 01.09.2020|Money replenishment|150.0
                 * 01.09.2020|Money transfer from Kur|200.0
                 */
        }

        @Test
        void whenGeneratePdf_expectedFilterTransactionByDate() throws IOException {
                TransactionCheck check1 = new TransactionCheck();
                TransactionCheck check2 = new TransactionCheck();
                TransactionCheck check3 = new TransactionCheck();
                TransactionCheck check4 = new TransactionCheck();
                Account acc = new Account();
                Account acc2 = new Account();
                Account acc3 = new Account();
                acc2.setId(2);
                acc3.setId(3);

                acc.setId(1);
                acc.setAccountNumber("123BYN");
                acc.setBalance(100);
                acc.setOpeningDate(LocalDateTime.now().minusYears(3));
                acc.setCurrency("BYN");
                acc.setUser(new User("Nikolai", "Urusov", "2003-01-01"));
                acc.setBank(new Bank("ClevertecBank"));

                acc2.setUser(new User("Peter", "Balk", "2001-12-12"));
                acc3.setUser(new User("Kur", "Sam", "1921-12-12"));

                check1.setOrigin(acc);
                check1.setTarget(acc2);
                check1.setTransferAmount(100);
                check1.setDescription(TransactionDescription.ACCOUNT_ACCOUNT_TRANSFER);
                check1.setCreatedAt(LocalDateTime.now().minusYears(5));

                check2.setOrigin(acc);
                check2.setTarget(acc);
                check2.setCreatedAt(LocalDateTime.now().minusYears(3));
                check2.setTransferAmount(50);
                check2.setDescription(TransactionDescription.ACCOUNT_TRANSFER_SUB);

                check3.setOrigin(acc);
                check3.setTarget(acc);
                check3.setCreatedAt(LocalDateTime.now().minusYears(2));
                check3.setTransferAmount(150);
                check3.setDescription(TransactionDescription.ACCOUNT_TRANSFER_ADD);

                check4.setOrigin(acc3);
                check4.setTarget(acc);
                check4.setCreatedAt(LocalDateTime.now().minusYears(1));
                check4.setTransferAmount(200);
                check4.setDescription(TransactionDescription.ACCOUNT_ACCOUNT_TRANSFER);

                List<TransactionCheck> whereOrigin = new ArrayList<>();
                whereOrigin.addAll(List.of(
                                check1,
                                check2,
                                check3));

                List<TransactionCheck> whereTarget = List.of(
                                check2,
                                check3,
                                check4);

                TransactionService transactionService = Mockito.mock(TransactionService.class);

                Mockito.when(transactionService.readAllWhereOriginAccountId(acc.getId())).thenReturn(
                                whereOrigin);

                Mockito.when(transactionService.readAllWhereTargetAccountId(acc.getId())).thenReturn(
                                whereTarget);

                generateAccountBankStatement = new GenerateAccountBankStatement(transactionService);

                generateAccountBankStatement.generateBankAccountStatement(
                                acc,
                                LocalDate.now().minusYears(3),
                                LocalDate.now());

                // expected following
                // |
                /*
                 * Bank statement
                 * Bank| ClevertecBank
                 * Client| Nikolai Urusov
                 * Account| 123BYN
                 * Currency| BYN
                 * Opening date| 01.09.2020
                 * Period| 01.09.2020 - 01.09.2023
                 * Date and time of creation| 01.09.2023, 22.42
                 * Balance| 100.0 BYN
                 * Date
                 * |
                 * Notes
                 * |
                 * Transfer
                 * ---------------------------------------------------------
                 * 01.09.2021|Money replenishment|150.0
                 * 01.09.2022|Money transfer from Kur|200.0
                 */
        }
}
