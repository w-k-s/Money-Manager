package io.wks.moneymanager.transaction;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import io.wks.moneymanager.transaction.converters.CategoryConverter;
import io.wks.moneymanager.transaction.converters.LocalDateConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TransactionDao {

    private final Table table;

    public TransactionDao(AmazonDynamoDB client) {
        this.table = new DynamoDB(client).getTable("Finance");
    }

    public CategoryTotals getTotalExpensesPerCategory(LocalDate from, LocalDate to, String createdBy) {
        final var scan = new ScanSpec()
                .withProjectionExpression("Category,Amount")
                .withFilterExpression("CreatedBy = :createdBy and TransactionDate BETWEEN :from and :to") // TODO: Add createdBy
                .withValueMap(new ValueMap()
                        .withString(":createdBy", createdBy)
                        .withString(":from", new LocalDateConverter().convert(from))
                        .withString(":to", new LocalDateConverter().convert(to))
                );

        final var result = table.scan(scan);
        final var iterator = result.iterator();

        final var totalsBuilder = CategoryTotals.builder();
        while (iterator.hasNext()) {
            var item = iterator.next();
            final var category = new CategoryConverter().unconvert(item.getString("Category"));
            final var amount = item.getNumber("Amount");
            totalsBuilder.addAmount(category, amount);
        }
        return totalsBuilder.build();
    }
}
