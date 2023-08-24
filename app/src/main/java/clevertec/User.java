package clevertec;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class User {
    private String id;

    private String firstName;

    private String lastName;

    private String birthDate;

    public User(String firstName,
            String lastName,
            String birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    private Set<Account> accounts;
}