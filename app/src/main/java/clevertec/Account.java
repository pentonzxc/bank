package clevertec;

import java.util.Optional;
import java.util.OptionalInt;

import lombok.Data;

@Data
public class Account {

    String id;

    Integer money = 0;

    Bank bank;

    User user;

    final Object LOCK = new Object();

    public Account(User user) {
        this.user = user;
    }

    // public?
    /* private */ public Account(User user, Bank bank, Integer money) {
        this(user, bank);
        this.money = money;
    }

    public Account(User user, Bank bank) {
        this(user);
        this.bank = bank;
        bank.addAccount(this);
    }

    public Integer addMoney(Integer money) {
        this.money += money;
        return this.money;
    }

    public Integer subMoney(Integer money) {
        if (this.money <= money) {
            return -1;
        }
        this.money -= money;
        return this.money;
    }

    public Integer transfer(Account target, int change) {
        if (subMoney(change) == -1) {
            return -1;
        }
        target.addMoney(change);
        return this.money;
    }
}
