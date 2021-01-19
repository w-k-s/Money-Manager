package io.wks.moneymanager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record Transaction(UUID uuid, String description, BigDecimal amount, LocalDate date, String createdBy){}
