package io.wks.moneymanager;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Document(collection = "transactions")
public record Transaction(UUID uuid,
                          Category category,
                          String description,
                          BigDecimal amount,
                          LocalDate date,
                          String createdBy) {
    public Transaction {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(category);
        Objects.requireNonNull(description);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(date);
        Objects.requireNonNull(createdBy);
    }

    @Id
    @Override
    public UUID uuid() {
        return uuid;
    }
}
