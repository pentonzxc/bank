package clevertec.service;

public class Instances {
    private Instances() {
    }

    private final static UserService userService = new UserService();
    private final static BankService bankService = new BankService();
    private final static AccountService accountService = new AccountService(bankService, userService);
    private final static TransactionService transactionService = new TransactionService(accountService);

    private final static BankStatementService bankStatementService = new BankStatementService(transactionService); 

    public static UserService userService() {
        return userService;
    }

    public static BankService bankService() {
        return bankService;
    }

    public static AccountService accountService() {
        return accountService;
    }

    public static TransactionService transactionService() {
        return transactionService;
    }

    public static BankStatementService bankStatementService() {
        return bankStatementService;
    }

}
