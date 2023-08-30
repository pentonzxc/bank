package clevertec;

import static clevertec.util.MoneyUtil.roundMoney;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import clevertec.transaction.check.TransactionCheck;
import lombok.NoArgsConstructor;

/**
 * Class that represents account in bank.
 */
@NoArgsConstructor
public class Account {

    private Integer id;

    private double balance = 0d;

    private Bank bank;

    private User user;

    private String currency;

    private LocalDateTime openingDate = LocalDateTime.now();

    private String accountNumber = UUID.randomUUID().toString();

    private final Object lock = new Object();

    /**
     * Add money to balance.
     * 
     * @param money - to add
     * @return double
     */
    public double addMoney(double money) {
        this.balance = roundMoney(this.balance + money);
        return this.balance;
    }

    /**
     * Take percent of balance and add to balance.
     * 
     * @param percent - percent of balance to add
     * @return double
     */
    public double addPercent(double percent) {
        balance = roundMoney(balance * (1 + percent / 100));
        return balance;
    }

    /**
     * Subtract money from balance.
     * 
     * @param money - to subtract
     * @return Double
     */
    public Double subMoney(double money) {
        if (this.balance < money) {
            return -1d;
        }
        this.balance = roundMoney(this.balance - money);
        return this.balance;
    }

    /**
     * Transfer money to target account.
     * 
     * @param target - where to transfer money
     * @param change - to transfer
     * @return double
     */
    public double transfer(Account target, double change) {
        if (subMoney(change) == -1) {
            return -1d;
        }
        target.addMoney(change);
        return this.balance;
    }

    /**
     * @return Integer
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return double
     */
    public double getBalance() {
        return balance;
    }

    /**
     * @param money
     */
    public void setBalance(double money) {
        this.balance = roundMoney(money);
    }


    
    /**
     * @return Bank
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * @param bank
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * @return User
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return Object
     */
    public Object getLock() {
        return lock;
    }

    /**
     * @return String
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Copy of account.
     * <p>
     * <b>Bank and User copy by references</b>.
     * 
     * @return Account
     */
    public Account softCopy() {
        Account copy = new Account();
        copy.setId(id);
        copy.setBank(bank);
        copy.setAccountNumber(accountNumber);
        copy.setUser(user);
        copy.setBalance(balance);

        return copy;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(LocalDateTime openingDate) {
        this.openingDate = openingDate;
    }

}
