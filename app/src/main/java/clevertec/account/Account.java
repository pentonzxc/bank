package clevertec.account;

import static clevertec.util.MoneyUtil.roundMoney;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import clevertec.bank.Bank;
import clevertec.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Class that represents account in bank.
 */

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Account {

    Integer id;

    double balance;

    Bank bank;

    User user;

    String currency;

    LocalDateTime openingDate;

    String accountNumber;

    @Setter(AccessLevel.NONE)
    @JsonIgnore
    transient private final Object lock = new Object();

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
     * @param money
     */
    public void setBalance(double money) {
        this.balance = roundMoney(money);
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
        copy.setOpeningDate(openingDate);
        copy.setCurrency(currency);
        copy.setUser(user);
        copy.setBalance(balance);

        return copy;
    }
}
