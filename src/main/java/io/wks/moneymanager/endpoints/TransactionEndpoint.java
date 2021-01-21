package io.wks.moneymanager.endpoints;

import io.wks.moneymanager.Category;
import io.wks.moneymanager.Transaction;
import io.wks.moneymanager.gen.*;
import io.wks.moneymanager.repository.TransactionRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.server.endpoint.annotation.SoapAction;

import javax.xml.datatype.DatatypeFactory;
import java.util.UUID;

import static io.wks.moneymanager.constants.Constants.NAMESPACE_URI;

@Endpoint
public class TransactionEndpoint {

    private final TransactionRepository transactionRepository;

    public TransactionEndpoint(TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "recordTransactionRequest")
    @ResponsePayload
    @Secured("hasRole('ROLE_ADMIN')")
    public RecordTransactionResponse recordTransaction(@RequestPayload RecordTransactionRequest request) {
        final var uuid = UUID.randomUUID();
        final var authentication = SecurityContextHolder.getContext().getAuthentication();

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

    //@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTransactionsByUuidRequest")
    @SoapAction(NAMESPACE_URI + "/transaction/findByUuid")
    @ResponsePayload
    public GetTransactionsResponse getTransactionsByUuid(@RequestPayload GetTransactionsByUuidRequest request) {
        return transactionRepository.findById(UUID.fromString(request.getUuid()))
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
                }).orElseThrow(() -> new TransactionNotFoundException(UUID.fromString(request.getUuid())));
    }
}
