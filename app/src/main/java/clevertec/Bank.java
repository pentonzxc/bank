package clevertec;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

/*
 * Class that represents bank system with accounts.
 */
public class Bank {
    Integer id;

    String name;

    // Set<Account> accounts;

    /**
     * Create empty bank.
     */
    public Bank() {
        // accounts = new HashSet<>();
    }

    /**
     * Create bank with name.
     */
    public Bank(String name) {
        this();
        this.name = name;
    }

    /**
     * Add account.
     * 
     * @param account
     */
    // public void addAccount(final Account account) {
    // accounts.add(account);
    // }

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
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Set<Account>
     */
    // public Set<Account> getAccounts() {
    // return accounts;
    // }

    // /**
    // * @param accounts
    // */
    // public void setAccounts(Set<Account> accounts) {
    // this.accounts = accounts;
    // }

}
