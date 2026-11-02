package safebank;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public final class Transaction {
    private final String id;
    private final TransactionType type;
    private final Money amount;
    private final LocalDateTime timestamp;

    public Transaction(String id, TransactionType type, Money amount) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

}


