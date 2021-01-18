package io.wks.moneymanager.endpoints;

import io.wks.moneymanager.Transaction;
import io.wks.moneymanager.gen.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.server.endpoint.annotation.SoapAction;

import javax.xml.datatype.DatatypeFactory;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.wks.moneymanager.constants.Constants.NAMESPACE_URI;

@Endpoint
public class TransactionEndpoint {

    private static final Map<UUID, Transaction> transactionStore = new ConcurrentHashMap();

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "recordTransactionRequest")
    @ResponsePayload
    public RecordTransactionResponse recordTransaction(@RequestPayload RecordTransactionRequest request) {
        final var uuid = UUID.randomUUID();
        transactionStore.put(uuid, new Transaction(
                uuid,
                request.getDescription(),
                request.getAmount(),
                request.getDate().toGregorianCalendar().toZonedDateTime().toLocalDate()
        ));

        final var response = new RecordTransactionResponse();
        response.setUuid(uuid.toString());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTransactionsByUuid")
    @SoapAction(NAMESPACE_URI+"/transaction/findByUuid")
    @ResponsePayload
    public GetTransactionsResponse getTransactionsByUuid(@RequestPayload GetTransactionsByUuidRequest request) {
        return Optional.ofNullable(transactionStore.get(UUID.fromString(request.getUuid())))
                .map(transaction -> {
                    try {
                        final var response = new TransactionResponse();
                        response.setAmount(transaction.getAmount());
                        response.setDescription(transaction.getDescription());
                        response.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(transaction.getDate().toString()));
                        response.setUuid(transaction.getUuid().toString());
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
