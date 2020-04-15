package no.unit.nva.customer.exception;

import nva.commons.exceptions.ApiGatewayException;

public class DynamoDBException extends ApiGatewayException {

    public DynamoDBException(String message, Exception exception) {
        super(exception, message);
    }

    @Override
    protected Integer statusCode() {
        return 502;
    }
}
