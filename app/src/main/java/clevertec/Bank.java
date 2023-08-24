package clevertec;

import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Bank {
    String id;

    String name;

    public Bank(String name) {
        this.name = name;
    }

    Set<Account> accounts;

    public void addAccount(final Account account) {
        accounts.add(account);
    }
}
