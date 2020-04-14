package no.unit.nva.customer.exception;

import nva.commons.exceptions.ApiGatewayException;

public class InputException extends ApiGatewayException {

    public InputException(String message) {
        super(message);
    }

    @Override
    protected Integer statusCode() {
        return 400;
    }
}
