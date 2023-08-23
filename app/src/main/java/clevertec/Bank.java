package clevertec;

import java.util.Set;

import lombok.Data;

@Data
public class Bank {
    String name;

    public Bank(String name) {
        this.name = name;
    }

    Set<Account> accounts;

    public void addAccount(final Account account) {
        accounts.add(account);
    }
}
