package clevertec;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

public class Bank {
    Integer id;

    String name;

    Set<Account> accounts;

    public Bank() {
        accounts = new HashSet<>();
    }

    public Bank(String name) {
        this();
        this.name = name;
    }

    public void addAccount(final Account account) {
        accounts.add(account);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

}
