package clevertec;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class User {
    private Integer id;

    private String firstName;

    private String lastName;

    private String birthDate;

    private Set<Account> accounts;

    public User() {
        accounts = new HashSet<>();
    }

    public User(String firstName,
            String lastName,
            String birthDate) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public void addAccount(Account acc) {
        accounts.add(acc);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    

}