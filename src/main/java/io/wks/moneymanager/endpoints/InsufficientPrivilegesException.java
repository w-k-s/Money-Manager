package io.wks.moneymanager.endpoints;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

import static io.wks.moneymanager.constants.Constants.NAMESPACE_URI;

@SoapFault(faultCode = FaultCode.CUSTOM,
        customFaultCode = "{" + NAMESPACE_URI + "}403_INSUFFICIENT_PRIVILEGES")
public class InsufficientPrivilegesException extends RuntimeException {
    public InsufficientPrivilegesException(String name, String privilege) {
        super(String.format("'%s' does not have the privilege: '%s'", name, privilege));
    }
}
