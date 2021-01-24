package io.wks.moneymanager.transaction;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import io.wks.moneymanager.transaction.converters.CategoryConverter;
import io.wks.moneymanager.transaction.converters.LocalDateConverter;
import io.wks.moneymanager.transaction.converters.UUIDConverter;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@DynamoDBTable(tableName = "Finance")
public class TransactionEntity {
    @Id
    private TransactionEntityId transactionId;
    private UUID uuid;
    private Category category;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private String createdBy;

    public TransactionEntity() {
    }

    public TransactionEntity(UUID uuid,
                             Category category,
                             String description,
                             BigDecimal amount,
                             LocalDate date,
                             String createdBy) {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(category);
        Objects.requireNonNull(description);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(date);
        Objects.requireNonNull(createdBy);

        this.uuid = uuid;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.createdBy = createdBy;
    }

    @DynamoDBHashKey(attributeName = "uuid")
    @DynamoDBTypeConverted(converter = UUIDConverter.class)
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @DynamoDBIndexHashKey(attributeName = "category", globalSecondaryIndexName = "categoryIndex")
    @DynamoDBTypeConverted(converter = CategoryConverter.class)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @DynamoDBAttribute(attributeName = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute(attributeName = "amount")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @DynamoDBIndexHashKey(attributeName = "createdBy", globalSecondaryIndexName = "createdByIndex")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @DynamoDBRangeKey(attributeName = "date")
    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", uuid=" + uuid +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }

    @DynamoDBIgnore
    public Transaction getModel() {
        return new Transaction(
                this.uuid,
                this.category,
                this.description,
                this.amount,
                this.date,
                this.createdBy
        );
    }
}
