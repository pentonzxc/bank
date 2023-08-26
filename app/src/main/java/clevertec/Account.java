package clevertec;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Account {

    private Integer id;

    private double money = 0d;

    private Bank bank;

    private User user;

    private String accountNumber = "1";

    private final Object lock = new Object();

    public double addMoney(double money) {
        this.money += money;
        return this.money;
    }

    public double addPercent(double percent) {
        money = money * (1 + percent / 100);
        return money;
    }

    public Double subMoney(double money) {
        if (this.money < money) {
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

    public Object getLock() {
        return lock;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    
    public Account softCopy() {
        Account copy = new Account();
        copy.setId(id);
        copy.setBank(bank);
        copy.setAccountNumber(accountNumber);
        copy.setUser(user);
        copy.setMoney(money);

        return copy;
    }

}
