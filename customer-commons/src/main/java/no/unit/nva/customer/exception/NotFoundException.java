package no.unit.nva.customer.exception;

import nva.commons.exceptions.ApiGatewayException;

public class NotFoundException extends ApiGatewayException {

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    protected Integer statusCode() {
        return 404;
    }
}
