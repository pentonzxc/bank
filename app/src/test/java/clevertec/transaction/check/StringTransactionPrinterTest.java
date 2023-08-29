package clevertec.transaction.check;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import clevertec.Account;
import clevertec.Bank;

public class StringTransactionPrinterTest {
    private TransactionPrinter<String> printer = TransactionPrinterFactory.stringPrinter();

    @Test
    void whenView_expectString() {
        String expected = """
                -------------------------------------------------------------------------

                |                           Банковский чек                              |

                |  Чек:                           c916bfac-b3ec-48d7-8116-bedaa81f9a6f  |

                |  2023-08-26:                                                17:43:25  |

                |  Тип транзакции:                                          Пополнение  |

                |  Банк отправителя:                                             Bank1  |

                |  Банк получателя:                                              Bank2  |

                |  Счёт отправителя:                                               123  |

                |  Счёт получателя:                                               1234  |

                |  Сумма:                                                   100.00 BYN  |

                -------------------------------------------------------------------------""";
        TransactionCheck check = new TransactionCheck();

        check.setDateTime(LocalDateTime.parse("2023-08-26T17:43:25", DateTimeFormatter.ISO_DATE_TIME));
        check.setDescription(ActionDescription.ACCOUNT_TRANSFER_ADD);
        check.setId(UUID.fromString("c916bfac-b3ec-48d7-8116-bedaa81f9a6f"));
        Account acc1 = new Account();
        Account acc2 = new Account();
        acc1.setBank(new Bank("Bank1"));
        acc1.setAccountNumber("123");
        acc2.setBank(new Bank("Bank2"));
        acc2.setAccountNumber("1234");
        check.setOrigin(acc1);
        check.setTarget(acc2);
        check.setTransferAmount(100);

        assertEquals(expected, printer.view(check));
    }

    @Test
    void whenTargetNumberAccountAndTargetBankEmpty_expectTargetPropertiesEqualOrigin() {
        String expected = """
                -------------------------------------------------------------------------

                |                           Банковский чек                              |

                |  Чек:                           c916bfac-b3ec-48d7-8116-bedaa81f9a6f  |

                |  2023-08-26:                                                17:43:25  |

                |  Тип транзакции:                                          Пополнение  |

                |  Банк отправителя:                                             Bank1  |

                |  Банк получателя:                                              Bank1  |

                |  Счёт отправителя:                                               123  |

                |  Счёт отправителя:                                               123  |

                |  Сумма:                                                   100.00 BYN  |

                -------------------------------------------------------------------------""";

        TransactionCheck check = new TransactionCheck();

        check.setDateTime(LocalDateTime.parse("2023-08-26T17:43:25", DateTimeFormatter.ISO_DATE_TIME));
        check.setDescription(ActionDescription.ACCOUNT_TRANSFER_ADD);
        Account acc1 = new Account();
        Account acc2 = new Account();

        check.setId(UUID.fromString("c916bfac-b3ec-48d7-8116-bedaa81f9a6f"));
        acc1.setBank(new Bank("Bank1"));
        acc1.setAccountNumber("123");
        check.setOrigin(acc1);
        check.setTransferAmount(100);

        assertEquals(expected, printer.view(check));
    }
}
