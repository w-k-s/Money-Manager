package io.wks.moneymanager.transaction;

import com.google.common.base.Preconditions;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Category(String topCategory, String middleCategory, String bottomCategory) {
    public Category {
        Objects.requireNonNull(topCategory);
        Optional.ofNullable(middleCategory).ifPresent(it -> Preconditions.checkArgument(!middleCategory.isBlank()));
        Optional.ofNullable(bottomCategory).ifPresent(it -> Preconditions.checkArgument(!bottomCategory.isBlank()));
    }

    public static Category of(List<String> names) {
        Preconditions.checkArgument(names.size() <= 3);
        final var top = names.get(0);
        final var mid = names.size() > 1 ? names.get(1) : null;
        final var bottom = names.size() > 2 ? names.get(2) : null;
        return new Category(top, mid, bottom);
    }

    public static Category valueOf(String category) {
        return Category.of(Arrays.asList(category.split("/")));
    }

    public List<String> asList() {
        return Stream.of(topCategory, middleCategory, bottomCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Category> asSelfAndParentCategoryList() {
        var list = new ArrayList<Category>();
        list.add(new Category(topCategory, null, null));
        if (this.middleCategory != null) {
            list.add(new Category(topCategory, middleCategory, null));
        }
        if (this.bottomCategory != null) {
            list.add(new Category(topCategory, middleCategory, bottomCategory));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public String toString() {
        return Stream.of(topCategory, middleCategory, bottomCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("/"));
    }
}
