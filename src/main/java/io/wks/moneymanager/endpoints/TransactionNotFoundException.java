package io.wks.moneymanager.endpoints;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

import java.util.UUID;

import static io.wks.moneymanager.constants.Constants.NAMESPACE_URI;

@SoapFault(faultCode = FaultCode.CUSTOM,
        customFaultCode = "{"+NAMESPACE_URI+"}404_TRANSACTION_NOT_FOUND")
public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(UUID transactionUUID) {
        super(String.format("Transaction %s not found", transactionUUID));
    }
}
