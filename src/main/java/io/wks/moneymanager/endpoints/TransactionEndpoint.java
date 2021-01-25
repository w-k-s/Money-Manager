package io.wks.moneymanager.endpoints;

import io.wks.moneymanager.gen.*;
import io.wks.moneymanager.transaction.Category;
import io.wks.moneymanager.transaction.Transaction;
import io.wks.moneymanager.transaction.TransactionService;
import io.wks.moneymanager.user.DefaultUser;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.addressing.server.annotation.Action;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static io.wks.moneymanager.constants.Constants.NAMESPACE_URI;
import static java.util.Collections.singletonList;

@Endpoint
public class TransactionEndpoint {

    private final TransactionService transactionService;

    public TransactionEndpoint(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "recordTransactionRequest")
    @ResponsePayload
    @Secured("hasRole('ROLE_ADMIN')")
    public RecordTransactionResponse recordTransaction(@RequestPayload RecordTransactionRequest request) {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        final var transaction = transactionService.saveTransaction(new Transaction(
                UUID.randomUUID(),
                Category.of(request.getCategory().getNames()),
                request.getDescription(),
                request.getAmount(),
                request.getDate().toGregorianCalendar().toZonedDateTime().toLocalDate(),
                authentication.getName()
        ));
        final var response = new RecordTransactionResponse();
        response.setUuid(transaction.uuid().toString());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTransactionsRequest")
    @ResponsePayload
    public GetTransactionsResponse getTransactions(@RequestPayload GetTransactionsRequest request) {
        final var authentication = (DefaultUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getTransactionsResponse(transactionService.getTransactionsBetweenDates(
                request.getFrom().toGregorianCalendar().toZonedDateTime().toLocalDate(),
                request.getTo().toGregorianCalendar().toZonedDateTime().toLocalDate(),
                authentication
        ));
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTransactionsByUuidRequest")
    @Action(NAMESPACE_URI + "/transaction/findByUuid")
    @ResponsePayload
    public GetTransactionsResponse getTransactionsByUuid(@RequestPayload GetTransactionsByUuidRequest request) {
        final var authentication = (DefaultUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getTransactionsResponse(singletonList(transactionService.findByUuid(
                UUID.fromString(request.getUuid()),
                authentication
        )));
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTotalExpensesPerCategoryRequest")
    @ResponsePayload
    public GetTotalExpensesPerCategoryResponse getTotalExpensesPerCategory(@RequestPayload GetTotalExpensesPerCategoryRequest request) throws DatatypeConfigurationException {
        final var authentication = (DefaultUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final var from = LocalDate.of(request.getYear(), request.getMonth(), 1);
        final var to = from.plusMonths(1).minusDays(1);
        final var categoryTotals = transactionService.getTotalExpensesPerCategory(
                request.getYear(),
                request.getMonth(),
                authentication
        );
        final var categoryTotalsResponse = new GetTotalExpensesPerCategoryResponse();
        categoryTotalsResponse.setFrom(DatatypeFactory.newInstance().newXMLGregorianCalendar(from.toString()));
        categoryTotalsResponse.setTo(DatatypeFactory.newInstance().newXMLGregorianCalendar(to.toString()));
        categoryTotals
                .stream()
                .map(it -> {
                    var total = new CategoryTotal();
                    total.setCategory(it.getKey().toString());
                    total.setTotal(it.getValue());
                    return total;
                })
                .forEach(it -> categoryTotalsResponse.getCategoryTotals().add(it));
        return categoryTotalsResponse;
    }

    private GetTransactionsResponse getTransactionsResponse(List<Transaction> transactions) {
        final var transactionsResponse = new GetTransactionsResponse();
        transactions.stream().map(transaction -> {
            try {
                final var category = new io.wks.moneymanager.gen.Category();
                category.getNames().addAll(transaction.category().asList());

                final var response = new TransactionResponse();
                response.setUuid(transaction.uuid().toString());
                response.setCategory(category);
                response.setAmount(transaction.amount());
                response.setDescription(transaction.description());
                response.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(transaction.date().toString()));
                response.setCreatedBy(transaction.createdBy());
                return response;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).forEach(it -> transactionsResponse.getTransactions().add(it));
        return transactionsResponse;
    }
}
