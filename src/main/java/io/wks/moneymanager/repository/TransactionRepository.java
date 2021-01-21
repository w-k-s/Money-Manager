package io.wks.moneymanager.repository;

import io.wks.moneymanager.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, UUID> { }
