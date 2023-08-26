package clevertec.transaction.check;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

public class StringTransactionPrinterTest {
    private TransactionPrinter<String> printer = TransactionPrinterFactory.stringPrinter();

    @Test
    void whenView_expectString() {
        String expected = """
                -------------------------------------------------------------------------

                |                           Банковский чек                              |

                |  Чек:                                                            123  |

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
        check.setId("123");
        check.setOriginBank("Bank1");
        check.setTargetBank("Bank2");
        check.setOriginAccountNumber("123");
        check.setTargetAccountNumber("1234");
        check.setMoney(100);

        assertEquals(expected, printer.view(check));
    }

    @Test
    void whenTargetNumberAccountAndTargetBankEmpty_expectTargetPropertiesEqualOrigin() {
        String expected = """
                -------------------------------------------------------------------------

                |                           Банковский чек                              |

                |  Чек:                                                            123  |

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
        check.setId("123");
        check.setOriginBank("Bank1");
        check.setOriginAccountNumber("123");
        check.setMoney(100);

        assertEquals(expected, printer.view(check));
    }
}
