package clevertec;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Account {

    Integer id;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Object getLOCK() {
        return LOCK;
    }



    
}
