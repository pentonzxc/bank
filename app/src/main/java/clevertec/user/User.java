package clevertec.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Class that represents owner of account(s) in bank(s).
 */

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class User {
    Integer id;

    String firstName;

    String lastName;

    String birthDate;

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
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }
}