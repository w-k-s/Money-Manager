package io.wks.moneymanager.repository;

import io.wks.moneymanager.Transaction;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;


@EnableScan
public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
    Optional<Transaction> findByUuid(UUID uuid);
}
