package io.wks.moneymanager.transaction;

import io.wks.moneymanager.endpoints.InsufficientPrivilegesException;
import io.wks.moneymanager.endpoints.TransactionNotFoundException;
import io.wks.moneymanager.user.DefaultUser;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionDao transactionDao;
    private final Enforcer enforcer;

    public TransactionService(TransactionRepository transactionRepository,
                              TransactionDao transactionDao,
                              Enforcer enforcer) {
        this.transactionRepository = transactionRepository;
        this.transactionDao = transactionDao;
        this.enforcer = enforcer;
    }

    public Transaction saveTransaction(Transaction transaction) {
        this.enforcer.addNamedPolicy("p", transaction.createdBy(), transaction.uuid().toString(), "read:entry");
        this.enforcer.addNamedPolicy("p", transaction.createdBy(), transaction.uuid().toString(), "write:entry");
        this.enforcer.savePolicy();

        transactionRepository.save(new TransactionEntity(
                transaction.uuid(),
                transaction.category(),
                transaction.description(),
                transaction.amount(),
                transaction.date(),
                transaction.createdBy()
        ));
        return transaction;
    }

    public List<Transaction> getTransactionsBetweenDates(LocalDate from,
                                                         LocalDate to,
                                                         DefaultUser user) {
        return transactionRepository.findByDateBetween(from, to).stream()
                .peek(it -> {
                    if (!this.enforcer.enforce(user, it, "read:entry")) {
                        throw new InsufficientPrivilegesException(user.getUsername(), "read:entry");
                    }
                })
                .map(TransactionEntity::getModel)
                .collect(Collectors.toList());
    }

    public Transaction findByUuid(UUID uuid, DefaultUser user) {
        return transactionRepository.findByUuid(uuid)
                .stream()
                .peek(it -> {
                    if (!this.enforcer.enforce(user, it, "read:entry")) {
                        throw new InsufficientPrivilegesException(user.getUsername(), "read:entry");
                    }
                }).map(TransactionEntity::getModel)
                .findFirst()
                .orElseThrow(() -> new TransactionNotFoundException(uuid));
    }

    public CategoryTotals getTotalExpensesPerCategory(int year, int month, DefaultUser user) {
        final var from = LocalDate.of(year, month, 1);
        final var to = from.plusMonths(1).minusDays(1);
        return transactionDao.getTotalExpensesPerCategory(from, to, user.getUsername());
    }
}
