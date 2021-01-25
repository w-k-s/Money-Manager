package io.wks.moneymanager.transaction;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class CategoryTotals {
    private final Map<Category, BigDecimal> totals;

    private CategoryTotals(Builder builder) {
        this.totals = Collections.unmodifiableMap(builder.getTotals());
    }

    public Stream<Map.Entry<Category, BigDecimal>> stream() {
        return totals.entrySet().stream();
    }

    @Override
    public String toString() {
        return totals.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private final Map<Category, BigDecimal> totals;

        private Builder() {
            this.totals = new HashMap<>();
        }

        private Map<Category, BigDecimal> getTotals() {
            return this.totals;
        }

        public Builder addAmount(Category category, BigDecimal bigDecimal) {
            category.asSelfAndParentCategoryList()
                    .forEach(node -> Optional.ofNullable(totals.get(node))
                            .ifPresentOrElse(
                                    total -> totals.put(node, total.add(bigDecimal)),
                                    () -> totals.put(node, bigDecimal)
                            )
                    );
            return this;
        }

        public CategoryTotals build() {
            return new CategoryTotals(this);
        }
    }
}
