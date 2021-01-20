package io.wks.moneymanager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record Transaction(UUID uuid, String description, BigDecimal amount, LocalDate date, String createdBy) {
    public Transaction {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(description);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(date);
        Objects.requireNonNull(createdBy);
    }
}
