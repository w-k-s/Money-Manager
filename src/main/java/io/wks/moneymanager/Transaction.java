package io.wks.moneymanager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Transaction {
    private final UUID uuid;
    private final String description;
    private final BigDecimal amount;
    private final LocalDate date;

    public Transaction(UUID uuid, String description, BigDecimal amount, LocalDate date) {
        this.uuid = uuid;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }
}
