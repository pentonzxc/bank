package clevertec;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class that represents owner of account(s) in bank(s).
 */
public class User {
    private Integer id;

    private String firstName;

    private String lastName;

    private String birthDate;

    private Set<Account> accounts;

    /**
     * Create empty user.
     */
    public User() {
        accounts = new HashSet<>();
    }

    /**
     * Create user.
     * 
     * @param firstName
     * @param lastName
     * @param birthDate
     */
    public User(String firstName,
            String lastName,
            String birthDate) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    /**
     * Add account
     * 
     * @param acc
     */
    public void addAccount(Account acc) {
        accounts.add(acc);
    }

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
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return String
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return String
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * @param birthDate
     */
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * @return Set<Account>
     */
    public Set<Account> getAccounts() {
        return accounts;
    }

    /**
     * @param accounts
     */
    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

}