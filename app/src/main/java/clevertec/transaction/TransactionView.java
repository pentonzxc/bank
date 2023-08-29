package clevertec.transaction;

import java.time.LocalDateTime;


public record TransactionView(String id, LocalDateTime begin, LocalDateTime end) {
}