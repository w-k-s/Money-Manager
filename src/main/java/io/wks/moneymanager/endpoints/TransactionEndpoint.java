package io.wks.moneymanager.endpoints;

import io.wks.moneymanager.Category;
import io.wks.moneymanager.Transaction;
import io.wks.moneymanager.gen.*;
import io.wks.moneymanager.repository.TransactionRepository;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.addressing.server.annotation.Action;

import javax.xml.datatype.DatatypeFactory;
import java.util.UUID;

import static io.wks.moneymanager.constants.Constants.NAMESPACE_URI;

@Endpoint
public class TransactionEndpoint {

    private final TransactionRepository transactionRepository;
    private final Enforcer enforcer;

    public TransactionEndpoint(TransactionRepository transactionRepository, Enforcer enforcer) {
        this.transactionRepository = transactionRepository;
        this.enforcer = enforcer;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "recordTransactionRequest")
    @ResponsePayload
    @Secured("hasRole('ROLE_ADMIN')")
    public RecordTransactionResponse recordTransaction(@RequestPayload RecordTransactionRequest request) {
        final var uuid = UUID.randomUUID();
        final var authentication = SecurityContextHolder.getContext().getAuthentication();

        this.enforcer.addNamedPolicy("p", authentication.getName(), uuid.toString(), "read:entry");
        this.enforcer.addNamedPolicy("p", authentication.getName(), uuid.toString(), "write:entry");

        this.enforcer.savePolicy();

        transactionRepository.save(new Transaction(
                uuid,
                Category.of(request.getCategory().getNames()),
                request.getDescription(),
                request.getAmount(),
                request.getDate().toGregorianCalendar().toZonedDateTime().toLocalDate(),
                authentication.getName()
        ));

        final var response = new RecordTransactionResponse();
        response.setUuid(uuid.toString());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTransactionsByUuidRequest")
    @Action(NAMESPACE_URI + "/transaction/findByUuid")
    @ResponsePayload
    public GetTransactionsResponse getTransactionsByUuid(@RequestPayload GetTransactionsByUuidRequest request) {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return transactionRepository.findById(UUID.fromString(request.getUuid()))
                .stream()
                .peek(it -> {
                    if (!this.enforcer.enforce(authentication.getName(), request.getUuid(), "read:entry")) {
                        throw new InsufficientPrivilegesException(authentication.getName(), "read:entry");
                    }
                })
                .map(transaction -> {
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
                }).map(resp -> {
                    final var responses = new GetTransactionsResponse();
                    responses.getTransactions().add(resp);
                    return responses;
                }).findFirst()
                .orElseThrow(() -> new TransactionNotFoundException(UUID.fromString(request.getUuid())));
    }
}
