package io.wks.moneymanager.transaction;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;


@EnableScan
public interface TransactionRepository extends CrudRepository<TransactionEntity, UUID> {
    Optional<TransactionEntity> findByUuid(UUID uuid);

    Collection<TransactionEntity> findByDateBetween(LocalDate from, LocalDate to);
}
