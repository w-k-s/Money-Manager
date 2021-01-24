package io.wks.moneymanager.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

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
}
