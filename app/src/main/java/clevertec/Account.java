package clevertec;

import java.util.Optional;
import java.util.OptionalInt;

import lombok.Data;

@Data
public class Account {

    String id;

    double money = 0d;

    Bank bank;

    User user;

    final Object LOCK = new Object();

    public Account(User user) {
        this.user = user;
    }

    // public?
    /* private */ public Account(User user, Bank bank, double money) {
        this(user, bank);
        this.money = money;
    }

    public Account(User user, Bank bank) {
        this(user);
        this.bank = bank;
        bank.addAccount(this);
    }

    public double addMoney(double money) {
        this.money += money;
        return this.money;
    }

    public double subMoney(double money) {
        if (this.money <= money) {
            return -1d;
        }
        this.money -= money;
        return this.money;
    }

    public double transfer(Account target, double change) {
        if (subMoney(change) == -1) {
            return -1d;
        }
        target.addMoney(change);
        return this.money;
    }
}
