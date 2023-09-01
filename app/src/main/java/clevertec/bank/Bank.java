package clevertec.bank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/*
 * Class that represents bank system with accounts.
 */

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Bank {
    Integer id;

    String name;

    /**
     * Create bank with name.
     */
    public Bank(String name) {
        this.name = name;
    }
}
